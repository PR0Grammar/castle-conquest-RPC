package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import shared.RPCMethods;

public class ClientHelper extends Thread{    
    private static String name = "ClientHelper";
    public static long time = System.currentTimeMillis();
    
    private Armory armory;
    private GameStatus gameStatus;
    private GateCoordinator gateCoordinator;

    private Socket connection;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean connected;
    private Belongings belongings;
    private EscapeRoutes escapeRoutes;
    // Keep track of the assigned for attackers/defenders, for other methods invoked
    private HashMap<String, Gate> assignedGate; 

    public ClientHelper(Socket s, int id, Armory a, GateCoordinator gc, Belongings b, EscapeRoutes er, GameStatus gs){
        connection = s;
        try{
            connected = true;
            gateCoordinator = gc;
            armory = a;
            belongings = b;
            escapeRoutes = er;
            gameStatus = gs;
            assignedGate = new HashMap<>();
            inputStream = new DataInputStream(s.getInputStream());
            outputStream = new DataOutputStream(s.getOutputStream());
            setName(name + "-" + id);
        }
        catch(Exception e){
            printError(e);
        }

    }
    
    // Returns an array of size 3: 
    // first element is name of client thread (string)
    // second is method it wants to invoke (integer)
    // third element is attacker/defender value (integer), but default is -1 if no value
    public Object[] readFromClient() throws IOException{
        Object[] rtr = new Object[3];

        try{
            rtr[0] = inputStream.readUTF();
            rtr[1] = inputStream.readInt();
            rtr[2] = inputStream.readInt();
        }
        catch(Exception e){
            // If the client closes the connection before we even get to read its request
            // we just end
            if(e instanceof EOFException){
                endConnection();
            }
            else{
                printError(e);
            }
        }

        return rtr;
    }

    public void writeToClient(int v){
        try{
            outputStream.writeInt(v);
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

    private void endConnection(){
        try{
            inputStream.close();
            outputStream.close();
            connection.close();
        }
        catch(Exception e){
            printError(e);
        }

        connected = false;
    }

    // Grab weapon FIFO order for attackers
    private void grabWeapon(String clientThreadName){
        armory.enterArmory(this, clientThreadName);

        int weaponVal = armory.getWeapon();
        // HAHA msg(clientThreadName + " has grabbed a weapon of value " + weaponVal);
        armory.leaveArmory(this, clientThreadName);
        
        // Let client know weapon value
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        writeToClient(weaponVal);
    }

    private void attackGate(String attackerName, int attackerValue){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        // Get a gate to attack
        gateCoordinator.attackerWaitForGate(this, attackerName);
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        Gate g = gateCoordinator.getGateToAttack(this, attackerName);
        assignedGate.put(attackerName, g);

        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        gateCoordinator.platoonAllowNextAttacker(this, attackerName);

        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        // Simulate "walking to" gate: 2-5s (gives King a chance)
        try{Thread.sleep((int) Math.ceil(Math.random() * 3000) + 2000);} catch(Exception e){}

        // Attack it
        g.attack(this, attackerName, attackerValue);

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        writeToClient(1);
    }

    private void defendGate(String defenderName, int defenderValue){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        // Get a gate to defend
        gateCoordinator.defenderWaitForGate(this, defenderName);
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        Gate g = gateCoordinator.getGateToDefend(this, defenderName);
        assignedGate.put(defenderName, g);

        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        gateCoordinator.platoonAllowNextDefender(this, defenderName);

        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        // Simulate "walking to" gate: 2-5s
        try{Thread.sleep((int) Math.ceil(Math.random() * 3000) + 2000);} catch(Exception e){}

        // Defend it
        g.defend(this, defenderName, defenderValue);

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        
        writeToClient(1);
    }

    private void defenderLeaveGate(String defenderName){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        // Leave the gate after defending
        Gate g = assignedGate.get(defenderName);
        assignedGate.remove(defenderName);

        g.defenderLeaveGate(this, defenderName);

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        writeToClient(1);  
    }

    private void attackerLeaveGate(String attackerName){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        // Leave the gate after attacking
        Gate g = assignedGate.get(attackerName);
        assignedGate.remove(attackerName);

        g.attackerLeaveGate(this, attackerName);

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        writeToClient(1);
    }

    private void rest(String playerName){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        msg(playerName + " is resting before the next battle");

        // Attackers/Defenders rest the same amount: between 6-7 seconds, 
        // which is at least 2x of king packing time
        try{
            Thread.sleep((int) Math.ceil(Math.random() * 1000) + 6000);
        }catch(Exception e){}

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        writeToClient(1);
    }

    private void waitForEscapeGate(String kingName){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        
        // Wait for escape route
        escapeRoutes.waitForEscapeRoute(this, kingName);

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        writeToClient(1);
    }

    private void packBelongings(String kingName){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }

        msg(kingName + " is packing his belongings...");
        belongings.pack();

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
        writeToClient(1);       
    }

    private void tryToEscape(String kingName){
        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
 
        escapeRoutes.tryToEscape(this, kingName);

        // Let client know we finished
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            escapeRoutes.noitfyKingGameOver();
            gateCoordinator.notifyEveryoneGameOver();
            writeToClient(-1);
            return;
        }
    
        writeToClient(1);        
    }
    
    public void run(){
        // HAHA msg("has started.");

        try{
            // Listen to requests from client
            while(connected){
                Object[] nextMethod = readFromClient();
                if(!connected) break;

                String threadName = (String) nextMethod[0];
                int methodToInvoke = (int) nextMethod[1];
                int attackerDefenderValue = (int) nextMethod[2];
 
                // First check if it is valid, print msg for the requets
                switch(methodToInvoke){
                    case(RPCMethods.END_CONNECTION): 
                    case(RPCMethods.GRAB_WEAPON):
                    case(RPCMethods.ATTACK_GATE):
                    case(RPCMethods.DEFEND_GATE):
                    case(RPCMethods.REST):
                    case(RPCMethods.WAIT_FOR_ESCAPE_GATE):
                    case(RPCMethods.PACK_BELONGINGS):
                    case(RPCMethods.TRY_TO_ESCAPE):
                    case(RPCMethods.ATTACKER_LEAVE_GATE):
                    case(RPCMethods.DEFENDER_LEAVE_GATE):
                        // HAHA msg(threadName + " has asked to execute " + RPCMethods.getMethodName(methodToInvoke) + ". Executing...");
                        break;
                    default:
                        msg(threadName + " has sent request, but method does not exist.");
                }
                
                // Next execute the method if request is valid
                switch(methodToInvoke){
                    case(RPCMethods.END_CONNECTION): // Special case
                        endConnection();
                        break;
                    case(RPCMethods.GRAB_WEAPON):
                        grabWeapon(threadName);
                        break;
                    case(RPCMethods.ATTACK_GATE):
                        attackGate(threadName, attackerDefenderValue);
                        break;
                    case(RPCMethods.DEFEND_GATE):
                        defendGate(threadName, attackerDefenderValue);
                        break;
                    case(RPCMethods.REST):
                        rest(threadName);
                        break;
                    case(RPCMethods.WAIT_FOR_ESCAPE_GATE):
                        waitForEscapeGate(threadName);
                        break;
                    case(RPCMethods.PACK_BELONGINGS):
                        packBelongings(threadName);
                        break;
                    case(RPCMethods.TRY_TO_ESCAPE):
                        tryToEscape(threadName);
                        break;
                    case(RPCMethods.ATTACKER_LEAVE_GATE):
                        attackerLeaveGate(threadName);
                        break;
                    case(RPCMethods.DEFENDER_LEAVE_GATE):
                        defenderLeaveGate(threadName);
                        break;
                    default:
                        break;
                }
            }
        }
        catch(Exception e){
            printError(e);
        }

        msg("has terminated.");
    }
}
