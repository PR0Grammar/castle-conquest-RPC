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

    // If response from server is ever -1, that means game has finished, and we should end connection
    public void run(){
        msg("has been created with defense value of " + getDefendValue());
        int res;
        
        try{
            while(!gameFinished){
                // // Defend a gate (includes choosing, waiting, battling)
                // requestServerRPC(RPCMethods.DEFEND_GATE);
                // res = serverResponse();

                // if(res == -1){
                //     gameFinished();
                //     continue;
                // }

                // // Rest after a battle
                // requestServerRPC(RPCMethods.REST);
                // res = serverResponse();

                // if(res == -1){
                //     gameFinished();
                //     continue;
                // }

                // // Temporary
                gameFinished();
            }
        }
        catch(Exception e){
            printError(e);
        }

        msg("has terminated.");
    }
}
