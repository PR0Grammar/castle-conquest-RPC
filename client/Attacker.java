package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Attacker extends Thread {
    private static String name = "Attacker";
    public static long time = System.currentTimeMillis();

    Socket connection;
    DataInputStream inputStream;
    DataOutputStream outputStream;

    public Attacker(Socket s, int id){
        connection = s;

        try{
            inputStream = new DataInputStream(s.getInputStream());
            outputStream = new DataOutputStream(s.getOutputStream());
            setName(name + "-" + id);
        }
        catch(Exception e){
            printError(e);
        }
    }

    public void msg(String m) {
    System.out.println(
        "["+
        (System.currentTimeMillis()-time)+
        "] " + 
        getName() +
        ": " +
        m);
    }

    public void printError(Exception e){
        System.out.println("Error from " + getName() + ": " + e);
        e.printStackTrace();
    }

    public void run(){
        msg("has been created.");
        //
        msg("has terminated.");
    }
}
