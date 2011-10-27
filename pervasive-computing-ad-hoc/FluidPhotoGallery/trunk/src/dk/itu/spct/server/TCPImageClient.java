package dk.itu.spct.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import dk.itu.spct.R;



/* todo:
 * 
 *  merge with TCPClient
 *  
 *  we can just have to constructors one for message support and the other for image
 *  
 */
public class TCPImageClient {

	private static final String TAG = "Fluidgallery";
    private static final boolean D = true;
	
    
    
    
    
 
    
    
    
    
	public static void send(Bitmap image) {
    
	
	if(D) Log.d(TAG, "TCPImageClient called");
    


//	DEVICEIP = TgetLocalIpAddress();
    
    
    String DEVICEIP = null;
    
    
    if(D) Log.d(TAG, "Device IP: " + DEVICEIP + " Image sent: " + image.toString());
    

    InetAddress receiversAddress;
	try {
//		receiversAddress = InetAddress.getByName("10.25.254.241"); // itu

		receiversAddress = InetAddress.getByName("10.0.1.7"); // home
		
        int receiversPort = 7656;
		
		// create a new socket
		Socket socket = new Socket( receiversAddress, receiversPort );
		
		OutputStream outputStream = socket.getOutputStream();
		// could have gotten an InputStream as well
		
		DataOutputStream dataOutputStream = new DataOutputStream( outputStream );
		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		boolean compressImage = image.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object   
		
		if (compressImage) {
		
			byte[] buffer = baos.toByteArray(); 
		
		
//		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
//		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos); 
//		byte[] bitmapdata = bos.toByteArray();
//		
//		//if bitmapdata is the byte array then getting bitmap goes like this
//		Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);
		
//		
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		while ((is.read(buffer)) != -1) {
//		    bos.write(buffer);
//		}
//
//		Bundle b = new Bundle();
//		b.putByteArray("data", bos.toByteArray());

		
//		int readData;
//		 while((readData=input.read(buffer))!=-1){
//			 dataOutputStream.write(buffer,0,readData);
//		 }
		
//		 dataOutputStream.write(buffer, 0, buffer.length);
			dataOutputStream.write(buffer, 0, buffer.length);

		if(D) Log.d(TAG, "buffer.length: " + buffer.length);
		
		
//		dataOutputStream.write(buffer);
		
//		dataOutputStream.
		
		//.writeByte(image);
		
//		 InputStream input = new FileInputStream(image);

//		 // try out reading length 
//		 InputStream input2 = new FileInputStream(image);
//		 byte[] buffer2 = null;
//		 int byte_length = input2.read(buffer2);
//		 System.out.println("byte_length: " + byte_length);
		 
		 
////		 byte[] buffer=new byte[1024*400]; // have seen variants byte[1024*2]  
//		 int readData;
//		 while((readData=input.read(buffer))!=-1){
//			 dataOutputStream.write(buffer,0,readData);
//		 }
		
		} else {
			Log.e(TAG, "image was not compressed succesfully");
		}
		
		
//		dataOutputStream.writeUTF(message);
		
		dataOutputStream.flush();
		
		
		// todo: clean me up 
		socket.close();

	
	} catch (UnknownHostException e) {
		if(D) Log.d(TAG, "error");
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		if(D) Log.d(TAG, "error");
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	}

	
	
	
	
	
	/* another try out */
    public static void send(View context) {
        
    	
    	if(D) Log.d(TAG, "TCPImageClient called");

    	
    	
        String DEVICEIP = null;
        
        
//        if(D) Log.d(TAG, "Device IP: " + DEVICEIP + " Image sent: " + image.toString());
        

        InetAddress receiversAddress;
    	try {
//    		receiversAddress = InetAddress.getByName("10.25.254.241"); // itu

    		receiversAddress = InetAddress.getByName("10.0.1.7"); // home
    		
            int receiversPort = 7656;
    		
    		// create a new socket
    		Socket socket = new Socket( receiversAddress, receiversPort );
    		
    		OutputStream outputStream = socket.getOutputStream();
    		// could have gotten an InputStream as well
    		
    		DataOutputStream dataOutputStream = new DataOutputStream( outputStream );
    	
//     	
    	InputStream input = context.getResources().openRawResource(R.drawable.medium_145_k); 
//        Bitmap image; 
//        try { image = BitmapFactory.decodeStream(input); }
//        finally { 
//        	try { input.close(); } catch(IOException e) {  } 
//        }
    
        
        
        
		 byte[] buffer=new byte[1024*400]; // have seen variants byte[1024*2]  
		 int readData;
		 while((readData=input.read(buffer))!=-1){
			 dataOutputStream.write(buffer,0,readData);
		 }
		 
        
        
		dataOutputStream.flush();
		
		
		// todo: clean me up 
		socket.close();

	
	} catch (UnknownHostException e) {
		if(D) Log.d(TAG, "error");
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		if(D) Log.d(TAG, "error");
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
        
        
    
    }

	
	
	
	
	
	
	
}
