package dk.itu.spct.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class TCPClient {

	private static final String TAG = "Fluidgallery - TCPClient";
    private static final boolean D = true;
	
	public static void send(String message) {
    
	
	if(D) Log.e(TAG, "called");
    


//    DEVICEIP = getLocalIpAddress();
    
    
    String DEVICEIP = null;
    
    
    if(D) Log.e(TAG, "Device IP: " + DEVICEIP + " Message sent: " + message);
    
    
    
    
    InetAddress receiversAddress;
	try {
		receiversAddress = InetAddress.getByName("10.25.254.241");

        int receiversPort = 7656;
		
		// create a new socket
		Socket socket = new Socket( receiversAddress, receiversPort );
		
		OutputStream os = socket.getOutputStream();
		// could have gotten an InputStream as well
		
		DataOutputStream dos = new DataOutputStream( os );
		
		
		
		dos.writeUTF(message);
		
		dos.flush();
		
		
		// todo: clean me up 
		socket.close();

	
	} catch (UnknownHostException e) {
		if(D) Log.e(TAG, "error");
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		if(D) Log.e(TAG, "error");
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	}
	
}
