package Server;

import java.util.Scanner;

public class User{

    private String username = "";
    private String password = "";

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    @SuppressWarnings("resource")
	public User() {
    	Scanner in = new Scanner(System.in);
    	System.out.println("Entrer le username");
    	this.username =  in.nextLine();
    	System.out.println("Entrer le password");
    	this.password =  in.nextLine();
	
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}