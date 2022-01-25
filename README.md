# UDP_Sim
 Simulated UDP Protocol with additional Sliding Window and Go-Back-N functionality. Part of University assignment.

Instructions:
1. Run UDPServer. This continuously waits for packets to be received from its socket (from UDPClient)
2. Run UDPClient, and enter an integer to define the sliding window. UDPClient reads the message from 'message.txt' and sends this to UDPServer. Sliding Window and Go-Back-N are both additional functions to ensure packets are received and acknowledged on both ends, and received in sequential order. 
3. UDPClient and UDPServer will terminate once the entire message has been successfully received and acknowledged by the server.