package lab2.aboa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.eclipse.jetty.util.ajax.JSON;

/**
 * 
 * Simple Tcp Client
 * 
 */
public class TcpClient implements Runnable {

	private URL pitlab;

	/**
	 * Simple Tcp Client constructor
	 * 
	 * @param port
	 * @param server_address
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public TcpClient(String server_address) throws IOException,
			ClassNotFoundException {
		this.pitlab = new URL(server_address);

	}

	@Override
	public void run() {
		URLConnection uc;
		try {
			uc = pitlab.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));

			StringBuilder sb = new StringBuilder();
			String inputLine = "";
			while ((inputLine = in.readLine()) != null)
				sb.append(inputLine);
			in.close();

			try {
				HashMap<String, String> obj = (HashMap<String, String>) JSON.parse(sb.toString());
				System.out.println(obj.get("location"));
			} catch (Exception e) {
				System.exit(0);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		TcpClient client = new TcpClient(
				"http://pit.itu.dk:7331/location-of/43:29:B1:55:00:00");
				
		Thread thread;
		
		while (true) {
			thread = new Thread(client);
			try {
				thread.start();
				thread.sleep(5000);
			} catch (InterruptedException e) {
				thread.interrupt();
			}
		}
		
		
	}

}
