package client;

import java.net.Socket;
import shared.RPCMethods;

public class Defender extends GameThread {
    private static String name = "Defender";
    private int defendValue;

    // Set random defend value upon creation, between 1-10
    private static int randomDefenseVal(){
        return (int) Math.ceil(Math.random() * 10);
    }

    public Defender(Socket s, int id){
        super(s, name + "-" + id);
        defendValue = randomDefenseVal();
    }


    public int getDefendValue(){
        return defendValue;
    }

    public void run(){
        msg("has been created.");
        
        try{
            while(true){
                requestServerRPC(RPCMethods.END_CONNECTION);
                closeConnections();
                break;
            }
        }
        catch(Exception e){
            printError(e);
        }

        msg("has terminated.");
    }
}
