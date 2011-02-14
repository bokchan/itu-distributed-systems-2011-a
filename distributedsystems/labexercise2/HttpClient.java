package pellekrogholt.labexercise2;

import java.io.*;
import java.net.*;

public class HttpClient {
	static final int PORT = 80;
	// what kind of user agents is it possible to specify
	// lets say we take a page from xdv theme'ed site - how would HttpClient be recognized ? ref. the problem i had with 'mailchimp'.
	static final String REQUEST= "GET / HTTP/1.1\nHost: www.itu.dk\nUser-Agent: none\n\n";
	static final String HOST = "www.itu.dk";
	
	public static void main(String[] args) throws Exception {
		Socket s = new Socket(HOST, PORT);
		
		OutputStream os = s.getOutputStream();
		PrintWriter to = new PrintWriter( os );
		
		InputStream is = s.getInputStream();
		InputStreamReader sr = new InputStreamReader( is );
		
	        to.print( REQUEST ); //send http-request
	        to.flush();
		StringBuffer sb = new StringBuffer();	

		while (!sr.ready()) ; //wait for reply from server

		int x=1;
		while (sr.ready()) { //read http-response
			char c = (char) sr.read();
			sb.append( c );
		}
		System.out.println(sb.toString());		
	}
}