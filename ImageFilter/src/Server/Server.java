package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;


public class Server {
    private static int port = 0;
    private static String ip = "";
    private static ServerSocket listener;
    private static int clientNumber = 0;


    public static void main(String[] args) throws IOException {
		
		  Verification ve = new Verification(); 
		  ve.initialisation(); 
		  ip = ve.getIp();
		  port = ve.getPort();

        System.out.println("Voici l'ip: " + ip + " et le port: " + port);
        
        listener = new ServerSocket();
        listener.setReuseAddress(true);
        InetAddress serverIp = InetAddress.getByName(ip);
        listener.bind(new InetSocketAddress(serverIp, port));

        System.out.format("The server is running on %s:%d%n", ip, port);

        try {
            while(true){
                new ClientHandler(listener.accept(), clientNumber++).start();
            }
        }
        finally{
            listener.close();
        }
    }
}