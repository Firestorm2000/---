package server;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@Singleton
@Path("/server")
public class Server {
	  private static final long serialVersionUID = 1L;
	  private static int messageLength = 3;
	  private Map<String, String> clients = new HashMap<>();    
	    
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public String getWork() {
		  byte[] message = randString(messageLength);
		    
		  MessageDigest md = null;
		  try {
			  md = MessageDigest.getInstance("MD5");
		  } catch (NoSuchAlgorithmException e) {
			  e.printStackTrace();
		  }

		  byte[] dig = md.digest(message);
		  String hash = DatatypeConverter.printHexBinary(dig).toUpperCase();

		  JSONObject responseObject = new JSONObject();
		  responseObject.put("hash", hash);
		  responseObject.put("length", message.length);
	
		  System.out.println("Client connected:\n" + responseObject.toJSONString());
	    
		  clients.put(hash, Base64.encodeBase64String(message));
		  
		  return responseObject.toJSONString();
	  }
	
	  @POST
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Response checkClientGuess(String requestBody) {
		  JSONObject requestBodyObject = (JSONObject) JSONValue.parse(requestBody); 
			
		  String clientHash = (String) requestBodyObject.get("hash");
		  byte[] clientGuess = Base64.decodeBase64((String)requestBodyObject.get("input"));
		  
		  if (!clients.containsKey(clientHash)) {
			  System.out.println("Error, client not found: " + clientHash);
			  return Response.status(406).build();
		  }
		  
		  byte[] realValue = Base64.decodeBase64(clients.get(clientHash));
			
		  if (Arrays.equals(clientGuess, realValue)) {
			  System.out.println("Successful attempt client: " + clientHash);
			 
			  clients.remove(clientHash);
			  return Response.status(200).build();
		  } else {
			  System.out.println("Fail attempt client: " + clientHash);
			  
			  return Response.status(406).build();
		  }
	  }
	  
	  private byte[] randString(int length)
	  {
	      byte[] array = new byte[length];
	      new Random().nextBytes(array);
	      
	      return array;
	  }
}