package client;

import java.net.Socket;
import shared.RPCMethods;

public class Attacker extends GameThread {
    private static String name = "Attacker";
    private int attackValue;

    public Attacker(int id){
        super(name + "-" + id);
    }

    // If response from server is ever -1, that means game has finished, and we should end connection
    public void run(){
        msg("has been created.");
        int res;

        try{
            // Get weapon from armory
            requestServerRPC(RPCMethods.GRAB_WEAPON);
            res = serverResponse();
            
            if(res == -1) gameFinished();
            else{ 
                attackValue = res;
                msg("has obtained weapon of value " + attackValue);
            }

            while(!gameFinished){
                // Attack a gate (includes choosing, waiting, battling)
                requestServerRPC(RPCMethods.ATTACK_GATE);
                res = serverResponse();

                if(res == -1){
                    gameFinished();
                    continue;
                }

                // Rest after a battle
                requestServerRPC(RPCMethods.REST);
                res = serverResponse();

                if(res == -1){
                    gameFinished();
                    continue;
                }
                
                //Temporary
                gameFinished();
            }
        }
        catch(Exception e){
            printError(e);
        }

        msg("has terminated.");
    }
}
