package Server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.imageio.ImageIO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientHandler extends Thread {

    private Socket socket;
    private int clientNumber;
    private String jsonFilePath = "./jsonFile.json";

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("Connection with client #" + clientNumber + " at " + socket);

    }
    
    
    @SuppressWarnings("unchecked")
	public boolean testUser(User user) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(jsonFilePath));
        JSONObject users = (JSONObject) obj;
        
        Set<Map.Entry<String,Object>> us = (Set<Map.Entry<String,Object>>) users.entrySet();
        if(us.isEmpty()) {
        	writeUser(user);
        	return true;
        }
        else {
        Iterator<Entry<String, Object>> it = us.iterator();
         while(it.hasNext()) {
        	 JSONObject u = (JSONObject)it.next().getValue();
        	  String username = u.get("username").toString();
        	  String password = u.get("password").toString();
        	 if(username.equals(user.getUsername()) &&  password.equals(user.getPassword())) {
        		 System.out.println("User found.... establishing connexion....");
        		 return true;
        	 }        	 
        	 else if(password.equals(user.getPassword()) && !username.equals(user.getUsername())) {
        		 writeUser(user);
        		 return true;
        	 }
        	 else if(username.equals(user.getUsername())){
        		 return false; 
        	 }
           }
         writeUser(user);
		 return true;
        }    
    }
    
    @SuppressWarnings("unchecked")
	public void writeUser(User user) throws FileNotFoundException, IOException, ParseException{ 	
    	
    	System.out.println("Creation of a new User");
    	JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(jsonFilePath));
        JSONObject users = (JSONObject) obj;
    	JSONObject newUser = new JSONObject();
    	newUser.put("password", user.getPassword());
    	newUser.put("username", user.getUsername());
    	users.put("user" + users.entrySet().size(), newUser);
 
        try { 
            FileWriter jsonFileWriter = new FileWriter(jsonFilePath);
            jsonFileWriter.write(users.toJSONString());
            jsonFileWriter.flush();
            jsonFileWriter.close();
                   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
			
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	
            User user = new User(in.readUTF(), in.readUTF());
            try {
            	if(testUser(user)) {
            		out.writeUTF("Correct password");
            		out.writeUTF("Welcome from server " + user.getUsername() + " \n" + "Please enter the image name");
            		  System.out.println("Connexion with " + user.getUsername() + " established");
					  String imagePath = ""; 
					  imagePath += in.readUTF();
					  System.out.println("[ "+ user.getUsername() + " - "+
					  socket.getInetAddress().toString() + ":" +  socket.getLocalPort() + " - " +
					  LocalDate.now() +"@"+ LocalTime.now() + " ] : "  + "Image " + imagePath );
					  
					 int size = in.readInt();
					 if(size != -1) {
						 
					 
					 int sizeLeft = size;
					 byte[] fileBytes = new byte[size];
					 int nRead = 0;
					 int off = 0;
					 try {
						 while ((nRead = in.read(fileBytes, off, 5000)) > 0) {
								sizeLeft -= nRead;
								off += nRead;
								if(sizeLeft <= 5000) {
									break;
								}
							}
						 in.read(fileBytes, off, sizeLeft);
					System.out.println("Image recieved");
					
					 }
					 
					 catch(Exception e) {
						 System.out.println("Not able to recieved Image for some reason");
						 
					 }
					 
					 ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
					 BufferedImage bImage2 = ImageIO.read(bis);
					 bImage2 = Sobel.process(bImage2);
					 ByteArrayOutputStream baos = new ByteArrayOutputStream();
					 ImageIO.write( bImage2, "jpg", baos );
					 baos.flush();
					 fileBytes = baos.toByteArray();
					 size = fileBytes.length;
					 
					 try {
					 out.writeInt(size);
					 out.write(fileBytes, 0, size);
					 }
					 catch(Exception e) {
						 System.out.println("Not able to send Image for some reason");
					 }
					 }
					 else {
						 System.out.println("Not able to recieve Image for some reason");
					 }
            	}
            	else {
            		System.out.println("wrong password");
            		out.writeUTF("Wrong password");
            	}
            	
			} catch (Exception e) {
				e.printStackTrace();
			}
        } catch (IOException e) {
            System.out.println("Error handlind client #" + clientNumber + " : " + e);
        }
        finally{
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("Not able to close the socket");
            }
            System.out.println("Connexion with client #" + clientNumber + " closed");
        }
    }
}