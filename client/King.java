package client;

import java.net.Socket;

import shared.RPCMethods;

public class King extends GameThread{
    private static String name = "King";

    public King(Socket s){
        super(s, name);
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