import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@WebServlet("/Master")
public class Master extends HttpServlet {
    private static String message = "";
    private static int messageLength = 5;
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        message = randString(messageLength);
        
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        if (md != null) {
            byte[] dig = md.digest(message.getBytes());
            String hash = DatatypeConverter.printHexBinary(dig).toUpperCase();

            JSONObject responseObject = new JSONObject();
            responseObject.put("hash", hash);
            responseObject.put("length", message.length());
            
            System.out.println("Sending object to client:\n" + responseObject.toJSONString());
            
            responseObject.writeJSONString(response.getWriter());
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line+"\n");
        }
        reader.close();

        JSONObject requestBodyObject = (JSONObject) JSONValue.parse(sb.toString()); 
        
        String clientGuess = (String) requestBodyObject.get("input");
        
        System.out.println("Recieved guess from client\nguess: " + clientGuess + "\nreal: " + message);
        
    	if (Arrays.equals(clientGuess.getBytes(), message.getBytes())) {
    		response.setStatus(200);
    	} else {
    		response.setStatus(406);
    	}
}

    private String randString(int length)
    {
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        
        System.out.println("GeneratedString = " + generatedString);
        
        return generatedString;
    }
}