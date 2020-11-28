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

    public GameThread(Socket s, String name) {
        connection = s;

        try {
            inputStream = new DataInputStream(s.getInputStream());
            outputStream = new DataOutputStream(s.getOutputStream());
            setName(name);
        } catch (Exception e) {
            printError(e);
        }
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
    public void requestServerRPC(int methodNumber) throws IOException {
        msg("requesting server to invoke " + RPCMethods.getMethodName(methodNumber));
        outputStream.writeUTF(getName());
        outputStream.writeInt(methodNumber);
    }

    // Read response from server
    // Values will only be string, up to implementation to determine type of value
    public String serverResponse() throws IOException{
        return inputStream.readUTF();
    }

    public void closeConnections() {
        msg("closing connection to server.");
        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (Exception e) {
            printError(e);
        }
    }
}
