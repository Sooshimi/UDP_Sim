//--------UDPClient---------//

import java.net.*;
import java.nio.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class UDPClient{

	private static int sending_num = 0; //points to the sending frame
	private static int SN = 0; //next sequence number tracker
	private static int i = 0; //for the for-loop when resending packets in window
	private static int window;

	public static void main(String args[]) throws Exception{

		// Create a socket
		DatagramSocket clientSocket = new DatagramSocket(); //client socket
		clientSocket.setSoTimeout(1000); //setting timeout when waiting for packets from server

		// read from file
		String data = new String(); //create new string for the read data
		try { //try-catch block for attempting to read file
			File f = new File("message.txt"); //new file object
			Scanner s = new Scanner(f); //create Scanner object for file
			data = s.nextLine(); //read lines
			s.close(); //close Scanner
		} catch (FileNotFoundException e) {
			System.out.println("File 'message.txt' not found."); //error message if file not found
		}

		//ask user input for window size (positive integer)
		System.out.print("Please enter window size: ");  //print user prompt for window size
		Scanner reader = new Scanner(System.in);  //ask user input

		while (!reader.hasNextInt()) { //while loop if input isn't an integer
			System.out.print("Please enter a positive integer for the window size: ");
			reader.next();
		}
		window = reader.nextInt(); //sets user input as the window size
		reader.close(); // close scanner
		System.out.println("");

		char[] array = data.toCharArray(); //convert message to a char array

		//WHILE loop, repeats while sending_num is lower than length of the message
		while (sending_num < data.length()) {

			char message = array[sending_num]; //gets the first char from the char array, starting from sending_num 0
			byte[] sendData = new byte[1024]; //define byte size

			InetAddress IPAddress = InetAddress.getByName("localhost"); // Get the IP address of the server

			// IF loop to continually send packages within the current window
			if (SN < (sending_num + window)) {

				// add the message and sending_num (sequence number of sending packet) to a Bytebuffer to send to server
				sendData = ByteBuffer.allocate(6).putInt(sending_num).putChar(message).array();

				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, IPAddress, 1234); //create Datagram packet
				clientSocket.send(packet); //send packet to socket for server to receive at its socket

				System.out.println("Packet seq num " + sending_num + " sent to Server: " + message);
				System.out.println("");

				SN++; //define next sequence number to send
				
				// limit set for SN when it reaches max length of message
				if (SN > data.length()) {
					SN--;
				}
			}

			//try-catch block when attempting to receive ACK packets from server.
			try {
				byte[] receiveData = new byte[1024];

				DatagramPacket received = new DatagramPacket(receiveData, receiveData.length); //create Datagram packet
				clientSocket.receive(received); //receive packet from server

				//receive sequence number of the packet received from server
				int receivedACK = ByteBuffer.wrap(received.getData()).getInt();

				//print ACK'ed sequence number
				System.out.println("Received ACK for packet seq num " + receivedACK + ": " + message);
				System.out.println("Window slides up.");
				System.out.println("");
				sending_num = receivedACK + 1; //sending_num increases by 1 above ACK'ed packet, to expect next ACK sequence number
			}
			catch( SocketTimeoutException exception ){
				//catch block to resend all packets in window, from sending_num to end of window
				
				SN = sending_num; //next expected sequence number goes back to the sending_num
				
				System.out.println("Timeout. Resending packages in current window from seq num " + SN);
				
				System.out.println("");
			}
		}
		System.out.println( "All packages sent to and acknowledged by Server. Session ended.");
		clientSocket.close(); //close socket once all packets have been sent to and acknowledged by the server
	}
}