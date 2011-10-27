package itu.socketprogramming;

import java.awt.Image;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.imageio.ImageIO;

/**
 * 
 * read image from 'disk' and send over tcp as bits - don't use image class
 * etc... since its apparently not supported by Android where its supposed to be
 * sent to...
 * 
 */
public class ImageTcpClient {
	
	private final static String imagePath = System.getProperty("user.dir") + ".dk.itu.socketprogramming.".replace('.', File.separator.charAt(0));


	public static Image readImageFromFile(String path) {

		Image image = null;

		try {
			// Read from a file
			File sourceimage = new File(path);
			image = ImageIO.read(sourceimage);
			
		} catch (IOException e) {

		}

		
		return image;

	}

	// Image image = null;
	// try {
	// // Read from a file
	// File sourceimage = new File("source.gif");
	// image = ImageIO.read(sourceimage);
	//
	// // Read from an input stream
	// InputStream is = new BufferedInputStream(
	// new FileInputStream("source.gif"));
	// image = ImageIO.read(is);
	//
	// // Read from a URL
	// URL url = new URL("http://java-tips.org/source.gif");
	// image = ImageIO.read(url);
	// } catch (IOException e) {
	// }

	public static void main(String args[]) throws Exception {

		
		String image = imagePath + "IMAG1168_tiny_12k.jpg";
//		String image = imagePath + "image_sample_1.jpg";
		

//		System.out.println(image);
//		System.out.println(readImageFromFile(image));
		
		  // local machine easiest
//		  InetAddress serverAddress = InetAddress.getByName("localhost");
//		  int serverPort = 7656;
		
		 //itu
		 // InetAddress serverAddress =InetAddress.getByName("10.25.254.241");
		 // int serverPort = 7656;
		
		
//		 // android device
		 InetAddress serverAddress = InetAddress.getByName("10.25.253.150");
		 int serverPort = 50230;
		
//		// 'home'
//		InetAddress serverAddress = InetAddress.getByName("10.0.1.12");
//		int serverPort = 7656;
		 


		 String message = "A Secret Message";
		
		 // create a new socket
		 Socket socket = new Socket( serverAddress, serverPort );
		
		 OutputStream outputStream = socket.getOutputStream();
		 // could have gotten an InputStream as well
		 
		 DataOutputStream dataOutputStream = new DataOutputStream( outputStream );

		 // 
		 
		 
		 InputStream input = new FileInputStream(image);

//		 // try out reading length 
//		 InputStream input2 = new FileInputStream(image);
//		 byte[] buffer2 = null;
//		 int byte_length = input2.read(buffer2);
//		 System.out.println("byte_length: " + byte_length);
		 
		 
		 byte[] buffer=new byte[1024*400]; // have seen variants byte[1024*2]  
		 int readData;
		 while((readData=input.read(buffer))!=-1){
			 dataOutputStream.write(buffer,0,readData);
		 }
		 
//		 InputStream input=new FileInputStream("image.png");
//		 byte[] buffer=new byte[1024];
//		 int readData;
//		 while((readData=input.read(buffer))!=-1){
//		 socketOutput.write(buffer,0,readData);
//		 }
		 
//		 dos.writeUTF(message);
		
		 dataOutputStream.flush();
		
		 socket.close();
	}
}