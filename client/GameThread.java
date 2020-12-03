package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import shared.RPCMethods;

// Thread that King, Defenders, and Attackers will extend
// to share common methods and member variables

public class GameThread extends Thread {
    public static long time = System.currentTimeMillis();

    Socket connection;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    public boolean gameFinished = false;

    public GameThread(String name) {
        setName(name);
    }

    public void startConnection(Socket s){
        msg("requesting to connect to server");
        
        connection = s;

        try {
            inputStream = new DataInputStream(s.getInputStream());
            outputStream = new DataOutputStream(s.getOutputStream());
        } catch (Exception e) {
            printError(e);
        }
        msg("is now connected to the server");
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    public void printError(Exception e) {
        System.out.println("Error from " + getName() + ": " + e);
        e.printStackTrace();
    }

    // Write to the server
    // First write is for thread name
    // Second write is method to invoke
    // Third is attacker/defender value. This should be -1 if no parameter

    public void requestServerRPC(int methodNumber, int attackerDefenderValue) throws IOException {
        msg("requesting server to invoke " + RPCMethods.getMethodName(methodNumber));
        outputStream.writeUTF(getName());
        outputStream.writeInt(methodNumber);
        outputStream.writeInt(attackerDefenderValue);
    }

    // Read response from server
    // Values will only be string, up to implementation to determine type of value
    public int serverResponse() throws IOException{
        return inputStream.readInt();
    }

    public void closeConnections() {
        msg("Closing connection to server since game finished.");
        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (Exception e) {
            printError(e);
        }
    }

    public void gameFinished() throws IOException{
        gameFinished = true;
        requestServerRPC(RPCMethods.END_CONNECTION, -1);
        closeConnections();
    }
}
