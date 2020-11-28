package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import shared.RPCMethods;

public class ClientHelper extends Thread{    
    private static String name = "ClientHelper";
    public static long time = System.currentTimeMillis();

    private Socket connection;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean connected;
    private Armory armory;

    public ClientHelper(Socket s, int id, Armory a){
        connection = s;

        try{
            connected = true;
            armory = a;
            inputStream = new DataInputStream(s.getInputStream());
            outputStream = new DataOutputStream(s.getOutputStream());
            setName(name + "-" + id);
        }
        catch(Exception e){
            printError(e);
        }

    }
    
    // Returns an array of size 2: 
    // first element is name of client thread (string)
    // second is method it wants to invoke (integer)
    public Object[] readFromClient() throws IOException{
        Object[] rtr = new Object[2];

        rtr[0] = inputStream.readUTF();
        rtr[1] = inputStream.readInt();

        return rtr;
    }

    public void writeToClient(String m){
        try{
            outputStream.writeUTF(m);
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
        msg(clientThreadName + " has grabbed a weapon of value " + weaponVal);
        armory.leaveArmory(this, clientThreadName);
        
        writeToClient(weaponVal + "");
    }
    
    public void run(){
        //msg("has started.");

        try{
            // Listen to requests from client
            while(connected){
                Object[] nextMethod = readFromClient();
                String threadName = (String) nextMethod[0];
                int methodToInvoke = (int) nextMethod[1];

                // First check if it is valid, print msg for the requets
                switch(methodToInvoke){
                    case(RPCMethods.END_CONNECTION): 
                    case(RPCMethods.GRAB_WEAPON):
                        //msg(threadName + " has asked to execute " + RPCMethods.getMethodName(methodToInvoke) + ". Executing...");
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
                    default:
                        break;
                }
            }
        }
        catch(Exception e){
            printError(e);
        }

        //msg("has terminated.");
    }
}
