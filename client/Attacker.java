package client;

import java.net.Socket;
import shared.RPCMethods;

public class Attacker extends GameThread {
    private static String name = "Attacker";
    private int attackValue;

    public Attacker(Socket s, int id){
        super(s, name + "-" + id);
    }

    public void run(){
        msg("has been created.");
        
        try{
            // Get weapon from armory
            requestServerRPC(RPCMethods.GRAB_WEAPON);
            attackValue = Integer.parseInt(serverResponse());
            msg("has obtained weapon of value " + attackValue);

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
