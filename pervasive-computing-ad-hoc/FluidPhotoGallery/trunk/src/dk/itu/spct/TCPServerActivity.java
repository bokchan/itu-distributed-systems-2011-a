package dk.itu.spct;

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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;



/*
 * partly based upon:
 * http://www.krvarma.com/2010/07/getting-ip-address-of-the-device-in-android
 * 
 * we are not fans off all the public void run() {
 * so perhaps look for a simpler implementation based on TcpServer.java in xxx
 * 
 */


public class TCPServerActivity extends Activity {

	
    private static final String TAG = "Fluidgallery";
    private static final boolean D = true;
	
    private TextView serverStatus;

    // default ip
    //public static String SERVERIP = "10.25.254.241";
    // note: a default seems confusing right ?
    public static String SERVERIP;

    // designate a port
    public static final int SERVERPORT = 50299; // not sure about this one

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	if(D) Log.d(TAG, "TCPServerActivity onCreate called");
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.serveractivity);
        serverStatus = (TextView) findViewById(R.id.server_status);

        SERVERIP = getLocalIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();
    }

    public class ServerThread implements Runnable {

        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                            if(D) Log.d(TAG, "Listening on IP: " + SERVERIP);
                            
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
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
                        	
                    			InputStream is = client.getInputStream();
                        	                        	
                    			DataInputStream in = new DataInputStream( is );
                    			final String message = in.readUTF();  // blocking call

                    			
                              handler.post(new Runnable() {
                              @Override
                              public void run() {
                              	
                            	  if(D) Log.d(TAG, "inside run before serverStatus.setText(message);");
                            	  
                              	// do whatever you want to the front end
                                  // this is where you can be creative

                              
                      			
                    			serverStatus.setText(message);
                    			 if(D) Log.d(TAG, message);
                              
                              
                              
                              
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
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                    if(D) Log.d(TAG, "Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
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
