package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int port = 3000;
    private static final int DEFAULT_NUMBER_OF_GATES = 2;
    private static final int DEFAULT_NUMBER_OF_SPACES = 3;
    
    private ServerSocket server;
    private int clientHelperCounter = 0;
    private int numOfGates = DEFAULT_NUMBER_OF_GATES;
    private int numOfSpaces = DEFAULT_NUMBER_OF_SPACES;
    private int totalCastleHealth;
    private Armory armory;
    private GateCoordinator gateCoordinator;
    private Castle castle;

    public void msg(String m){
        System.out.println("[Server]: " + m);
    }
    
    // Initialize Gates, GateCoordinator, Castle
    public void initVariables(){
        msg("Creating Armory...");
        armory = new Armory();
        msg("Creating Castle...");
        castle = new Castle(totalCastleHealth);
        msg("Creating GateCoordinator...");
        gateCoordinator = new GateCoordinator(numOfGates, numOfSpaces);
    }

    public void startServer(){
        try{
            // Create server
            server = new ServerSocket(port);
            msg("has started. Listening on port " + port);
            msg("Ready to clients...");

            // ClientHelper threads
            while(true){
                Socket s = server.accept();
                msg("connected to new client. Creating ClientHelper thread with id = " + clientHelperCounter);
                (new ClientHelper(
                    s, 
                    clientHelperCounter++,
                    armory,
                    gateCoordinator,
                    castle
                )).start();
            }
        }
        catch(Exception e){
            System.out.println("Server error: " + e);
            e.printStackTrace();
        }
    }

    public Server(){
        numOfGates = DEFAULT_NUMBER_OF_GATES;
        numOfSpaces = DEFAULT_NUMBER_OF_SPACES;

        initVariables();
        startServer();
    }

    public static void main(String[] args){
        // TODO: Allow for variable num gates/spaces
        new Server();
    }
}
