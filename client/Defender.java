package client;

import shared.RPCMethods;

public class Defender extends GameThread {
    private static String name = "Defender";
    private int defendValue;

    // Set random defend value upon creation, between 1-10
    private static int randomDefenseVal(){
        return (int) Math.ceil(Math.random() * 10);
    }

    public Defender(int id){
        super(name + "-" + id);
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
                // Defend a gate (includes choosing, waiting, battling)
                requestServerRPC(RPCMethods.DEFEND_GATE, defendValue);
                res = serverResponse();

                if(res == -1){
                    gameFinished();
                    continue;
                }

                // Leave gate after battle
                requestServerRPC(RPCMethods.DEFENDER_LEAVE_GATE, -1);
                res = serverResponse();

                if(res == -1){
                    gameFinished();
                    continue;
                }

                // Rest after a battle
                requestServerRPC(RPCMethods.REST, -1);
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
