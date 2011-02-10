package distributedsystems.labexercise2;

import java.net.*;
import java.io.*;

public class UdpSender {
  public static void main(String args[ ]) throws Exception{ 
		InetAddress receiversAddress = InetAddress.getByName("localhost");
		int receiversPort = 6789;		                                                 
	  DatagramSocket datagramSocket = new DatagramSocket();
		String messageAsString = "Hello";
		byte [ ] message = messageAsString.getBytes();
		int messageLength = messageAsString.length();
		DatagramPacket datagramPacket = new DatagramPacket(message, messageLength, receiversAddress, receiversPort);
    datagramSocket.send( datagramPacket );
  }
}