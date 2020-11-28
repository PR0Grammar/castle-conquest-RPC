package client;

import java.net.Socket;
import shared.RPCMethods;

public class Defender extends GameThread {
    private static String name = "Defender";

    public Defender(Socket s, int id){
        super(s, name + "-" + id);
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
