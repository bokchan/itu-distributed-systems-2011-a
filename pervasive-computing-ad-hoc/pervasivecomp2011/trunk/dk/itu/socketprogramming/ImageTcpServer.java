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




/**
 * 
 * can serve multiple client connections
 *
 */
public class ImageTcpServer {

	private int port;
	private int number = 1;
	private final static String imagePath = System.getProperty("user.dir") + ".dk.itu.socketprogramming.output.".replace('.', File.separator.charAt(0));
	
	/* constructor */
	public ImageTcpServer(int port) throws IOException {
		this.port = port; 
		ServerSocket server_socket = new ServerSocket( port );

		// this part handles multiple connections / users
		while(true) {
			Socket client_socket = server_socket.accept(); // blocking call code bellow not executed before request from client
			System.out.println("client_socket.getLocalAddress() :" + client_socket.getLocalAddress());
			System.out.println("client_socket.getRemoteSocketAddress() :" + client_socket.getRemoteSocketAddress());
//			RemoteSocket: /10.25.233.246:53910
//			LocalAddress: /10.25.250.231
//			InetAddress: /10.25.233.246
			
			Connection c = new Connection(client_socket);
			new Thread(c).start(); 
		}  
	}

	//class Connection extends Thread {
	/**
	 * Connection(s)
	 * 
	 * Class that can handle multiple connections - its based on xxx (add reference)
	 * but its made private and for that reason moved into / under the server class
	 * because it should be accessible by that class.
	 * 
	 * It implements threads with use of the Runnable and not the Thread 
	 * 
	 */
	private class Connection implements Runnable {

//		private ObjectInputStream ois; // previously in
//		private ObjectOutputStream oos; // previously out
		
		//private DataInputStream  dataInputStream; // previously in
//		private DataOutputStream out; // previously out
		
		 
		private String message;
		private Socket socket;

		private Connection (Socket socket) throws IOException {

			this.socket = socket;
			//out = new OutputStream( socket.getOutputStream());
//			oos = new AppendableObjectOutputStream( socket.getOutputStream());
			InputStream inputStream = socket.getInputStream();
			
			DataInputStream dataInputStream = new DataInputStream( inputStream );

			
			byte buffer[] = new byte[1024 * 15];
//			byte buffer[] = new byte[1024 * 300];
			
			// here the data input stream bytes is read/stored into the buffer array
			dataInputStream.read(buffer); // blocking call
			
			// now go from array of bytes to an image
		    InputStream bufferedInputStream = new ByteArrayInputStream(buffer);
		    BufferedImage image = ImageIO.read(bufferedInputStream);	    

	    	String imagePathFileId = imagePath + "image_send_from_client"+ number+".jpg";
	    	System.out.println( "imagePathFileId: " +  imagePathFileId);
		    
		    try {
			        File outputfile = new File(imagePath + "image_send_from_client"+ number+".jpg");
			        ImageIO.write(image, "jpg", outputfile);
		    	} catch (IOException e) {
		    }
		    
		    bufferedInputStream.close(); // remember to close the image buffer / not the socket thats handled by the client :)			
			
		    System.out.println( "BufferedImage image: " +  image);
//    	    
		    System.out.println( "buffer[]: " +  buffer.toString());
			
//			message = dataInputStream.readUTF();  // blocking call
//			System.out.println( message );

			// could also been written in a one line
//			String message = ((DataInputStream) dis).readUTF(); // blocking call
			
		    
		    // increment 
		    
		    number++;
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub			
		}

	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main (String args[]) throws Exception{ 

//		int serverPort = 7656;
//
//		//10.25.254.241
		
		ImageTcpServer server = new ImageTcpServer(7656);
	}
	
}