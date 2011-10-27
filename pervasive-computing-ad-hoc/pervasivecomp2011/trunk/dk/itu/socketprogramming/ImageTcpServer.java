package itu.socketprogramming;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class ImageTcpServer{
	
	
	private final static String imagePath = System.getProperty("user.dir") + ".dk.itu.socketprogramming.output.".replace('.', File.separator.charAt(0));
	
	
	
	
	public static void main (String args[]) throws Exception{

		
		
		
		int serverPort = 7656;

		//10.25.254.241
		
		ServerSocket serverSocket = new ServerSocket( serverPort );
		Socket socket = serverSocket.accept(); // blocking call
		
		// code bellow this is not executed before 
		// a connection from a client is done
		
		
		// now we have got a socket from the client
		
		// note: here we are on the socket object not the socket server 
		InputStream inputStream = socket.getInputStream();  
		
		// could have gotten an OutputStream as well
		DataInputStream dataInputStream = new DataInputStream( inputStream  );
		// note: is from the io lib
		// A data input stream lets an application read primitive Java 
		// data types from an underlying input stream in a machine-independent way.
		

		//String message = dataInputStream.readUTF(); // blocking call

		// todo: figure out the right way to bet buffer length : 
		
		byte buffer[] = new byte[1024 * 15];
//		byte buffer[] = new byte[1024 * 300];
		
		// here the data input stream bytes is read/stored into the buffer array
		dataInputStream.read(buffer); // blocking call
		
		// now go from array of bytes to an image
	    InputStream in = new ByteArrayInputStream(buffer);
	    BufferedImage image = ImageIO.read(in);	    
		

    	String imagePathFileId = imagePath + "image_send_from_client.jpg";
    	System.out.println( "imagePathFileId: " +  imagePathFileId);
	    
	    try {
	    	
	        File outputfile = new File(imagePath + "image_send_from_client.jpg");
	        ImageIO.write(image, "jpg", outputfile);
	    } catch (IOException e) {
	    }
		
		
		
//		BufferedInputStream bufInputStream = new BufferedInputStream(dataInputStream); 
//
//	    byte buffer[] = new byte[1024 * 2]; 
//
//	    int len; 
//
//	    int total = 0; 
//	    
////	    // or ((len = bufInputStream.read(buf)) > 0) {
////	    while ((len = bufInputStream.read(buffer)) != -1) { 
////
////	        total += len; 
////
////	    } 
//	    
//
////	    // 
////	    InputStream in = new ByteArrayInputStream(buffer);
////	    BufferedImage image = ImageIO.read(in);	    
//	    
//	    
//	    bufInputStream.close();
//
	    System.out.println( "BufferedImage image: " +  image);
//	    	    
	    System.out.println( "buffer[]: " +  buffer.toString());
//	    
//	    
//	    
////	    Image image = Toolkit.getDefaultToolkit().createImage(
////	               byteArrayOut.toByteArray());
//	    
//	    
//	    // naive try out
////	    BufferedImage bi = ImageIO.read(buf);
//	    
//		
//	    System.out.println( "image read of length: " +  total);
//		
		
//		BufferedImage bIResult = null;
//		// Fill your bufferedImage
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		boolean resultWrite = ImageIO.write(bIResult, "PNG", bos);
//		byte[] imageInBytes = bos.toByteArray();
//		int length = imageInBytes.length;
		
		

		
//		System.out.println( message ) ;      
	}    
}