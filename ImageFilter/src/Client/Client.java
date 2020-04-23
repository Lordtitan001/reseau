package Client;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class Client {
	private static Socket socket;
	private static String ip = "";
	private static int port = 0;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnknownHostException, IOException {

		  Verification ve = new Verification();
		  ve.initialisation(); 
		  ip = ve.getIp();
		  port = ve.getPort();

		socket = new Socket(ip, port);
		System.out.format("The server is running on %s:%d%n", ip, port);

		User user = new User();

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		out.writeUTF(user.getUsername());
		out.writeUTF(user.getPassword());

		DataInputStream in = new DataInputStream(socket.getInputStream());
		String state = in.readUTF();
		
		if(state.equals("Correct password")) {
		String HelloMessage = in.readUTF();
		System.out.println(HelloMessage);

		Scanner input = new Scanner(System.in);
		String imageName = input.next();
		out.writeUTF(imageName);

		try {
			File imgFile = new File(imageName);
			byte[] fileBytes = Files.readAllBytes(imgFile.toPath());
			out.writeInt(fileBytes.length);
			out.write(fileBytes, 0, fileBytes.length);
			System.out.format("Image: %s send to the server", imageName);
			System.out.println("");
		} catch (Exception e) {
			System.out.println("Image not found");
			out.writeInt(-1);
			socket.close();
			System.out.println("You have been disconected");
			System.exit(-1);
			
		}

		int size = in.readInt();
		byte[] imageBytes = new byte[size];
		int nRead = 0;
		int off = 0;
		int sizeLeft = size;
		
		try {
			while ((nRead = in.read(imageBytes, off, 5000)) > 0) {
				sizeLeft -= nRead;
				off += nRead;
				if(sizeLeft <= 5000) {
					break;
				}
			}
			in.read(imageBytes, off, sizeLeft);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
			BufferedImage bImage2 = ImageIO.read(bis);
			ImageIO.write(bImage2, "jpg", new File("output.jpg"));			
			System.out.println("Image created, this is the file location: ./output.jpg");
			
		}
		catch(Exception e) {
			System.out.println(" Image recieved but not able to create the file for some reason. Here is the exection message :" + e.getMessage());
			socket.close();
			System.out.println("You have been disconected");
			System.exit(-1);
		}
			

		}
		else {
			System.out.println(state);
		}
		socket.close();
		System.out.println("You have been disconected");

	}
}