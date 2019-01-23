
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import org.apache.catalina.tribes.util.Arrays;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Slave {
	private static Long startTime = new Date().getTime();
	
   public static void main(String[] args) {
	   while(true) {
		   HttpURLConnection conn = null;
		   
		   try {
			URL url = new URL("http://localhost:8080/MasterSlaves/Master");
			conn = (HttpURLConnection)url.openConnection();
	           conn.setRequestMethod("GET");
	           conn.connect();
	           
	           BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	           StringBuilder sb = new StringBuilder();
	           String line;
	           while ((line = br.readLine()) != null) {
	               sb.append(line+"\n");
	           }
	           br.close();
	
	           JSONObject responseObject = (JSONObject) JSONValue.parse(sb.toString()); 
	           
	           crackTheCode((String)responseObject.get("hash"),(long) responseObject.get("length"));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
		    }
		   
		   System.out.println("Requesting new string");
	   }
   }

    private static void crackTheCode(String hash, long length) {
    	System.out.println("Code from server received \nhash:" + hash + "\nlength: " + length);
    	
    	while(true) {
    		HttpURLConnection conn = null;
    		   
    		   try {
    			URL url = new URL("http://localhost:8080/MasterSlaves/Master");
    			conn = (HttpURLConnection)url.openConnection();
    	        conn.setRequestProperty("content-type","application/json; charset=utf-8");
    			conn.setRequestMethod("POST");
    			conn.setDoOutput(true);
	           JSONObject requestObject = new JSONObject();
	           requestObject.put("hash", hash);
	           requestObject.put("input", guessTheInput((int)length, hash));
	            
	            System.out.println("Sending guess to server:\n" + requestObject.toJSONString());
	            
	            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            requestObject.writeJSONString(wr);
	            wr.flush();
	            
	           conn.connect();
	           if (conn.getResponseCode() == 200) {
	        	   System.out.println("Successfully cracked code with length " + length);
	        	   System.out.println("Time taken: " + ((new Date().getTime() - startTime) / 1000) + " seconds");
	        	   break;
	           }else {
	        	   System.out.println("Wrong guess, continuing work!");
	        	   continue;
	           }
	         
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
		           
			    }
    	}
    }

	public static String guessTheInput(int length, String hash)
    {
        byte[] array = new byte[length];
        String generatedString = null;
        String guessHash = null;
        do {        	
        	new Random().nextBytes(array);
	        generatedString = new String(array, Charset.forName("UTF-8"));
	         
	        MessageDigest md = null;
	        try {
	            md = MessageDigest.getInstance("MD5");
	            
	            if (md != null) {
	                byte[] dig = md.digest(generatedString.getBytes());
	                guessHash = DatatypeConverter.printHexBinary(dig).toUpperCase();
	            }
	        } catch (NoSuchAlgorithmException e) {
	        	e.printStackTrace();
	        }
           	        
        } while (!Arrays.equals(guessHash.getBytes(), hash.getBytes()));
        
        System.out.println("possible guess");
        return generatedString;
    }
}