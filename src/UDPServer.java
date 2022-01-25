
//---------UDPServer --------//

import java.net.*;
import java.nio.*;

class UDPServer {

	public static void main(String args[]) throws Exception {

		//create Datagram Socket for the server with port number
		DatagramSocket serverSocket = new DatagramSocket(1234);

		byte[] receiveData = new byte[1024]; //set bytes of received data
		byte[] sendData = new byte[1024]; //set bytes of send data
		int nextSN = 0; //tracks expected next ACK seq number
		boolean loop = true;

		System.out.println("Waiting for packets from client...\r\n");

		//WHILE loop to continue while waiting for packets from client
		while (loop) {

			try {
				// create empty receive packet
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				//socket receives packet
				serverSocket.receive(receivePacket);

				//get the sequence number of the package received
				int seqNum = ByteBuffer.wrap(receivePacket.getData()).getInt();

				// create string object of received packet data
				String s = new String(receivePacket.getData(), 0, receivePacket.getLength());

				System.out.println("Received packet seq num " + seqNum + " from client ");

				//randomise number between 0-1
				var num = Math.random();

				// simulate packet dropping on server side, creating chance of responding to the message
				if (num <= 0.7) {

					//IP address of receiver
					InetAddress IPAddress = receivePacket.getAddress();

					//port of receiver
					int port = receivePacket.getPort();

					// if the sequence number of the received packet is the expect sequence number, then send the ACK of the seq num to client
					if (seqNum == nextSN) {
						
						//add sequence number of the acknowledged package
						sendData = ByteBuffer.allocate(4).putInt(seqNum).array();
						
						//create data packet to send ACK to receiver
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

						//send ACK packet to receiver server socket
						serverSocket.send(sendPacket);
						
						//print the message when successfully ACK'ed
						System.out.println("Sent ACK seq num "+ seqNum + " to client: " + s.charAt(s.length()-1) + "\r\n");	
						
						nextSN++; //expect next sequence number from next receieved packets
					}
				}
				else {
					//else don't do anything
					System.out.println( "Packet with seq num "+ seqNum + " was dropped\r\n");
				}

				//if the next expected seq num goes above length of message ('umbrella'), then close the socket and break the loop
				if (nextSN>7) {
					System.out.println("Message received successfully from client. Session ended.");
					serverSocket.close();
					loop = false;
				}
			} catch( SocketException exception ){} //catch socket expections
		}
	}
}