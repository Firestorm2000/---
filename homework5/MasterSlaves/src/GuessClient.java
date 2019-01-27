import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.DatatypeConverter;

import org.apache.catalina.tribes.util.Arrays;
import org.apache.tomcat.util.codec.binary.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GuessClient {
  public static void main(String[] args) {
	  while(true) {
		  System.out.println("Get work from server");
		  Client client = ClientBuilder.newClient(new ClientConfig());
		  WebTarget service = client.target(getBaseURI());

		  String serverResponse = service.path("rest").path("server").request().get(String.class);
		  JSONObject responseObject = (JSONObject) JSONValue.parse(serverResponse); 
		   
		  Long startTime = new Date().getTime();
		  crackTheCode((String)responseObject.get("hash"),(long) responseObject.get("length"));
		  System.out.println("Time taken: " + ((new Date().getTime() - startTime)) + "ms");
	   	}
  }
  
  private static void crackTheCode(String hash, long length) {
  	System.out.println("Code from server received \nhash:" + hash + "\nlength: " + length);
  	
  	while(true) {
  		Client client = ClientBuilder.newClient(new ClientConfig());
  		WebTarget service = client.target(getBaseURI());

  		JSONObject requestObject = new JSONObject();
  		requestObject.put("hash", hash);
  		requestObject.put("input", guessTheInput((int)length, hash));
            
  		System.out.println("Sending guess to server:\n" + requestObject.toJSONString());
  		Response serverResponse = service.path("rest").path("server").request(MediaType.APPLICATION_JSON).post(Entity.json(requestObject.toJSONString()));
            
  		if (serverResponse.getStatus() == 200) {
  			System.out.println("Successfully cracked code with length " + length);
  			break;
  		}else {
  			System.out.println("Wrong guess, continuing work!");
  			continue;
  		}
  	}
  }

	public static String guessTheInput(int length, String hash)
  {
      byte[] array = new byte[length];
      String guessHash = null;
      do {        	
      	new Random().nextBytes(array);
	         
	        MessageDigest md = null;
	        try {
	            md = MessageDigest.getInstance("MD5");
	            
	            if (md != null) {
	                byte[] dig = md.digest(array);
	                guessHash = DatatypeConverter.printHexBinary(dig).toUpperCase();
	            }
	        } catch (NoSuchAlgorithmException e) {
	        	e.printStackTrace();
	        }
         	        
      } while (!Arrays.equals(guessHash.getBytes(), hash.getBytes()));
      
      System.out.println("possible guess");
      
      return Base64.encodeBase64String(array);
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8080/MasterSlaves/").build();
  }
}