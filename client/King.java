package client;

import shared.RPCMethods;

public class King extends GameThread{
    private static String name = "King";

    public King(){
        super(name);
    }

    // If response from server is ever -1, that means game has finished, and we should end connection
    public void run(){
        msg("has been created.");
        
        try{
            while(!gameFinished){
                gameFinished();
            }
        }
        catch(Exception e){
            printError(e);
        }

        msg("has terminated.");
    }
}