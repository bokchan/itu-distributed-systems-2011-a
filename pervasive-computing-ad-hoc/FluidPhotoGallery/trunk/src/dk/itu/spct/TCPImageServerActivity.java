package dk.itu.spct;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;



/*
 * partly based upon:
 * http://www.krvarma.com/2010/07/getting-ip-address-of-the-device-in-android
 * 
 * we are not fans off all the public void run() {
 * so perhaps look for a simpler implementation based on TcpServer.java in xxx
 * 
 */


public class TCPImageServerActivity extends Activity {


	
    private static final String TAG = "Fluidgallery";
    private static final boolean D = true;
	
    private TextView serverStatus;

    // default ip
    //public static String SERVERIP = "10.25.254.241";
    // note: a default seems confusing right ?
    public static String androidTcpServerIp;

    // designate a port
    private static final int androidTcpServerPort = 50230; // dont use 50299 used in xxx

    // dosn't work tried various approaches ?  when cat log printed its 50299
    // but is catch'ed below off error...
    // private int androidTcpServerPort = R.integer.android_tcp_server_port;
    //     
    //R.integer.android_tcp_server_port; // not sure about this one
    
    
    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	if(D) Log.d(TAG, "TCPServerActivity onCreate called");
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.imageserveractivity);
        serverStatus = (TextView) findViewById(R.id.server_status);

        androidTcpServerIp = getLocalIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();
    }

    public class ServerThread implements Runnable {

        public void run() {
            try {
                if (androidTcpServerIp != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + androidTcpServerIp);
                            if(D) Log.d(TAG, "Listening on IP: " + androidTcpServerIp);
                            
                        }
                    });
                    serverSocket = new ServerSocket(androidTcpServerPort);
                    while (true) {
                        // listen for incoming clients
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Connected.");
                                if(D) Log.d(TAG, "Connected.");
                            }
                        });

                        try {
                        	
                    			InputStream inputStream = client.getInputStream();
                        	                        	
                    			DataInputStream dataInputStream = new DataInputStream( inputStream );
                    			
                    			
//                    			final String message = dataInputStream.readUTF();  // blocking call

                    			byte buffer[] = new byte[1024 * 15];
//                    			byte buffer[] = new byte[1024 * 300];
                    			
                    			// here the data input stream bytes is read/stored into the buffer array
                    			dataInputStream.read(buffer); // blocking call
                    			
                    			// now go from array of bytes to an image
                    		    InputStream bufferedInputStream = new ByteArrayInputStream(buffer);

                    		    final Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                    		    
                    		    // not sure if this could have been done simpler / more direct ? 
//                    		    .decodeByteArray(data, offset, length)
//                    		    .decodeStream(bufferedInputStream);
                    		    
                    		    // android.graphics.BitmapFactory
                    		    // 
                    		    // Creates Bitmap objects from various sources, including files, streams, and byte-arrays.
                    		    
                    		    if(D) Log.d(TAG, "image should have been received!?");
                    		    if(D) Log.d(TAG, "bitmap.getHeight(): " + bitmap.getHeight() + " - expected: 151 px");
                    		    if(D) Log.d(TAG, "bitmap.getWidth(): " + bitmap.getWidth() + " - expected: 151 px");
                    		    

                    		    
                    		    // BufferedImage image = ImageIO.read(bufferedInputStream);
                    			
                    			
//                    		      // Get reference to ImageView 
//                    	        countryIcon = (ImageView) row.findViewById(R.id.country_icon); 
//
//                    	      Bitmap bitmap = BitmapFactory.decodeStream(this.context.getResources().getAssets() 
//                    	                    .open(imgFilePath)); 
//                    	       countryIcon.setImageBitmap(bitmap);
                    			
                    			
                    			
                              handler.post(new Runnable() {
                              @Override
                              public void run() {
                              	
                            	  if(D) Log.d(TAG, "inside run before serverStatus.setText(message);");
                            	  
                              	// do whatever you want to the front end
                                  // this is where you can be creative

                              
                            	  if (bitmap.getHeight() > 0) serverStatus.setText("image recieved from table top");
//                    			serverStatus.setText(message);
//                    			 if(D) Log.d(TAG, message);

                            	  
                            	 // have to be inside the run() - hmm have to clean all that up
                            	  
//                      		    (TextView) findViewById(R.id.server_status);
                      		    ImageView imageFromClient = (ImageView) findViewById(R.id.image_from_client);
                      		    
                      		    imageFromClient.setImageBitmap(bitmap);
                              
                              
                              
                              }
                              });
                    			
                    			
// approach for reading more that a message
                    			
                    			
//							BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));                            
//                            String line = null;
//                            while ((line = in.readLine()) != null) {                                
//                                if(D) Log.d(TAG, line);
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                    	String message = line;
//                                    	serverStatus.setText(message);
//                                    	
//                                    	// do whatever you want to the front end
//                                        // this is where you can be creative
//                                    }
//                                });
//                            }
                            break;
                        } catch (Exception e) {
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
//                                    if(D) Log.d(TAG, "Oops. Connection interrupted. Please reconnect your phones.");
//                                }
//                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                            if(D) Log.d(TAG, "Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                        if(D) Log.e(TAG, "Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    // gets the ip address of your phone's network
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            if(D) Log.d(TAG, ex.toString());
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
             // make sure you close the socket upon exiting
             serverSocket.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

}
