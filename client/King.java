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
        
        int res;
        try{
            while(!gameFinished){
                // Wait for gate to open up for escaping
                requestServerRPC(RPCMethods.WAIT_FOR_ESCAPE_GATE, -1);
                res = serverResponse();

                if(res == -1){
                    gameFinished();
                    continue;
                }

                // Packing belongings
                requestServerRPC(RPCMethods.PACK_BELONGINGS, -1);
                res = serverResponse();

                if(res == -1){
                    gameFinished();
                    continue;
                }

                // Try to escape
                requestServerRPC(RPCMethods.TRY_TO_ESCAPE, -1);
                res = serverResponse();

                if(res == -1){
                    gameFinished();
                    continue;
                }
            }
        }
        catch(Exception e){
            printError(e);
        }

        msg("has terminated.");
    }
}