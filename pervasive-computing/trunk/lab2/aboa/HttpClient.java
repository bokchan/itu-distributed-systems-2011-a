package lab2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.eclipse.jetty.util.ajax.JSON;

public class HttpClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL pitlab = new URL(" http://pit.itu.dk:7331/location-of/43:29:B1:55:00:00");
        URLConnection uc = pitlab.openConnection();
        
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                uc.getInputStream()));
        
        
        
        StringBuilder sb = new StringBuilder();
        String inputLine = "";
        while ((inputLine = in.readLine()) != null) 
            sb.append(inputLine);
        in.close();
        
        HashMap<String, String> obj = (HashMap<String, String>) JSON.parse(sb.toString()); 
        System.out.println(obj.get("location"));
        
	}	
}
