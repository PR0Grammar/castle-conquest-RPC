package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientHelper extends Thread{
    private static String name = "ClientHelper";
    public static long time = System.currentTimeMillis();

    private Socket connection;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientHelper(Socket s, int id){
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

    private void closeConnection(){
        try{
            inputStream.close();
            outputStream.close();
            connection.close();
        }
        catch(Exception e){
            printError(e);
        }
    }

    public void run(){
        msg("has started.");
        //
        msg("has terminated.");
    }
}
