package Server;

import java.io.IOException;
import java.util.Scanner;

public class Verification {

    private static int port = 0;
    private static String ip = "";

    public Verification(){}
    
    public static boolean validate(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }

    public Verification initialisation() throws IOException {
        @SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
        boolean ipVerification = true;
        while (ipVerification) {
            System.out.println("Entrer l'IP du poste");
            ip = in.next();
            if (validate(ip)) {
                ipVerification = false;
            }
        }

        boolean portTest = true;
        System.out.println("Entrer le port d'ecoute");
        while (portTest) {
            port = in.nextInt();
            if (port < 5000 || port > 5050) {
                System.out.println("La valeur doit etre comprise entre 5000 et 5050");
            } else {
                portTest = false;
            }
        }
       
        return this;
    }
    public int getPort(){
        return port;
    }

    public String getIp(){
        return ip;
    }
}