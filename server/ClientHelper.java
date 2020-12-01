package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import shared.RPCMethods;

public class ClientHelper extends Thread{    
    private static String name = "ClientHelper";
    public static long time = System.currentTimeMillis();
    
    private Armory armory;
    private Castle castle;
    private GateCoordinator gateCoordinator;

    private Socket connection;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean connected;
    private Belongings belongings;

    public ClientHelper(Socket s, int id, Armory a, GateCoordinator gc, Castle c, Belongings b){
        connection = s;
        try{
            connected = true;
            castle = c;
            gateCoordinator = gc;
            armory = a;
            belongings = b;
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
        
        writeToClient(weaponVal);
    }

    private void attackGate(String attackerName, int attackerValue){
        // Get a gate to attack
        gateCoordinator.attackerWaitForGate(this, attackerName);        
        Gate g = gateCoordinator.getGateToAttack(this, attackerName);
        gateCoordinator.platoonAllowNextAttacker(this, attackerName);

        // Attack it
        g.attack(this, attackerName, attackerValue);

        // Leave the gate after attacking
        g.attackerLeaveGate(this, attackerName);

        // Let client know we finished
        writeToClient(1);
    }

    private void defendGate(String defenderName, int defenderValue){
        // Get a gate to defend
        gateCoordinator.defenderWaitForGate(this, defenderName);
        Gate g = gateCoordinator.getGateToDefend(this, defenderName);
        gateCoordinator.platoonAllowNextDefender(this, defenderName);

        // Defend it
        g.defend(this, defenderName, defenderValue);
        
        // Leave the gate after defending
        g.defenderLeaveGate(this, defenderName);

        // Let client know we finished
        writeToClient(1);
    }

    private void rest(String playerName){
        msg(playerName + " is resting before the next battle");

        //TODO
        try{Thread.sleep(6000);}catch(Exception e){}

        // Let client know we finished
        writeToClient(1);
    }

    private void waitForEscapeGate(String kingName){
        //TODO

        // Let client know we finished
        writeToClient(1);
    }

    private void packBelongings(String kingName){
        belongings.pack();

        // Let client know we finished
        writeToClient(1);       
    }

    private void tryToEscape(String kingName){
        //TODO

        // Let client know we finished
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
                    default:
                        break;
                }
            }
        }
        catch(Exception e){
            printError(e);
        }

        // HAHA msg("has terminated.");
    }
}
