package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    private GameStatus gameStatus;
    private Belongings belongings;
    private EscapeRoutes escapeRoutes;

    public void getInitialDataFromClient() {
        msg("Waiting for client to send num of gates, spaces, and castle health...");
        try {
            Socket connection = server.accept();
            msg("Connected to client.");
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            DataInputStream dis = new DataInputStream(connection.getInputStream());


            numOfGates = dis.readInt();
            msg("Received num of gates: " + numOfGates);
            numOfSpaces = dis.readInt();
            msg("Received num of spaces per gate: " + numOfSpaces);
            totalCastleHealth = dis.readInt();
            msg("Received total castle heatlh: " + totalCastleHealth);

            msg("Replying to client to let them know we got the data");
            dos.writeInt(1); // Let client know we are good to go.

            // Close connections
            dos.close();
            dis.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Server error: " + e);
            e.printStackTrace();
        }
    }

    public void msg(String m) {
        System.out.println("[Server]: " + m);
    }

    // Initialize Gates, GateCoordinator, Castle
    public void initVariables() {
        msg("Creating GameStatus...");
        gameStatus = new GameStatus();
        msg("Creating EscapeRoutes...");
        escapeRoutes = new EscapeRoutes(gameStatus);
        msg("Creating Armory...");
        armory = new Armory(gameStatus);
        msg("Creating Belongings...");
        belongings = new Belongings();
        msg("Creating Castle...");
        castle = new Castle(totalCastleHealth);
        msg("Creating GateCoordinator...");
        gateCoordinator = new GateCoordinator(numOfGates, numOfSpaces, gameStatus, castle, escapeRoutes);
    }

    public void startServer() {
        try {
            // Create server
            server = new ServerSocket(port);
            msg("has started. Listening on port " + port);

            // Get initial variables
            getInitialDataFromClient();
            initVariables();

            msg("Ready to clients...");

            // ClientHelper threads
            while (true) {
                Socket s = server.accept();
                // HAHA msg("connected to new client. Creating ClientHelper thread with id = " + clientHelperCounter);
                (new ClientHelper(s, clientHelperCounter++, armory, gateCoordinator, belongings, escapeRoutes, gameStatus)).start();
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e);
            e.printStackTrace();
        }
    }

    public Server() {
        numOfGates = DEFAULT_NUMBER_OF_GATES;
        numOfSpaces = DEFAULT_NUMBER_OF_SPACES;

        startServer();
    }

    public static void main(String[] args) {
        new Server();
    }
}
