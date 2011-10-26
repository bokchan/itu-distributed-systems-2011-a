package dk.itu.spct.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import dk.itu.spct.GalleryActivity;
import dk.itu.spct.R;
import dk.itu.spct.R.id;
import dk.itu.spct.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ServerConnect extends Activity {
	
	private TextView info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serverconnect);
		info = (TextView) findViewById(R.id.info);
	}
	
	public void onConnectClick(View view) {
		info.setText("Connect to 188.94.218.188: 4569");
		
		serverConnect("188.94.218.188", 4569);
	}
	
	public void onGotoGallery(View view) {
		Intent intent = new Intent(this, GalleryActivity.class);
		startActivity(intent);
	}
	
	private void serverConnect(String server, int port) {
		try {
			Socket s = new Socket(server, port);
			s.setReuseAddress(true);
			
			OutputStream os = s.getOutputStream();
			PrintWriter pw = new PrintWriter(os);
			pw.println("hello from android");
			
//			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			String response = reader.readLine();
//			info.setText("Response: " + response);
			
			pw.close();
			//reader.close();
			s.close();
	
		}
		catch (UnknownHostException e) {
			info.setText(e.getMessage());
		}
		catch (IOException e) {
			info.setText(e.getMessage());
		}
	}
}