package pellekrogholt.labexercise2;

import java.net.*;
import java.io.*;

public class UdpReceiver{
  public static void main(String args[ ]) throws Exception{ 
		int receiversPort = 6789;		                                                 
	  DatagramSocket datagramSocket = new DatagramSocket( receiversPort );
		byte[ ] buffer = new byte[1000]; // max 65.536 bytes
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		datagramSocket.receive( datagramPacket ); // pelle: blocking call code bellow is not executed before sender sends a datagram 
		String message = new String( datagramPacket.getData() );
		message = message.trim(); //removes white spaces
		System.out.println( message );
  }
}