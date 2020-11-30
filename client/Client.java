package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private static int DEFAULT_ATTACKER_COUNT = 10;
    private static int DEFAULT_DEFENDER_COUNT = 10;
    private static int DEFAULT_GATE_COUNT = 2;
    private static int DEFAULT_SPACE = 3;
    private static int port = 3000;
    private static String host = "localhost";

    public ArrayList<Attacker> attackers = new ArrayList<>();
    public ArrayList<Defender> defenders = new ArrayList<>();
    public King king;

    public int attackerCount;
    public int defenderCount;
    public int gateCount;
    public int space;

    public void msg(String m) {
        System.out.println("[Client]: " + m);
    }

    public int getTotalCastleDefense() {
        int sum = 0;
        for (Defender d : defenders) {
            sum += d.getDefendValue();
        }
        return sum;
    }

    public void sendInitialData() {
        msg("Waiting to connect to server to send over num of gates, spaces, and castle health");
        try {
            Socket connection = new Socket(host, port);
            msg("Connected to server. Sending over initial data...");
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            DataInputStream dis = new DataInputStream(connection.getInputStream());

            dos.writeInt(gateCount);
            msg("Sent num of gates");
            dos.writeInt(space);
            msg("Sent num of spaces per gate");
            dos.writeInt(getTotalCastleDefense());
            msg("Sent castle health");

            msg("Waiting for server to reply..");
            int res = dis.readInt(); // Wait for server to send 1, so we know when to begin

            // Close connections
            dis.close();
            dos.close();
            connection.close();

            if (res != 1) {
                throw new Error("Expected to get 1 from server, instead received " + res);
            }

            msg("Server replied.");
        } catch (Exception e) {
            System.out.println("Client error: " + e);
        }
    }

    public void startGame() {
        msg("Starting game.");

        for (Attacker a : attackers) {
            a.start();
        }

        for (Defender d : defenders) {
            d.start();
        }

        king.start();
    }

    public void createThreads() {
        // create attackers
        for (int i = 0; i < attackerCount; i++) {
            attackers.add(new Attacker(i));
        }

        // create defenders
        for (int i = 0; i < defenderCount; i++) {
            defenders.add(new Defender(i));
        }

        // create king
        king = (new King());

    }

    // Request connection to server for each thread
    // This must be done AFTER we send the initial data to server
    public void connectThreadsToServer() {
        try {
            for(Attacker a: attackers){
                a.startConnection(new Socket(host, port));
            }

            for(Defender d: defenders){
                d.startConnection(new Socket(host, port));
            }
            
            king.startConnection(new Socket(host, port));
        } catch (Exception e) {

        }
    }

    public Client() {
        msg("has started.");
        attackerCount = DEFAULT_ATTACKER_COUNT;
        defenderCount = DEFAULT_DEFENDER_COUNT;
        gateCount = DEFAULT_GATE_COUNT;
        space = DEFAULT_SPACE;

        // Create threads
        createThreads();

        // Send server initial data
        sendInitialData();

        // Request connnection to server for each thread
        connectThreadsToServer();

        // Start game
        startGame();
    }

    public Client(int a, int d, int g, int s) {
        msg("has started.");
        attackerCount = a;
        defenderCount = d;
        gateCount = g;
        space = s;

        // Create threads
        createThreads();

        // Send server initial data
        sendInitialData();

        // Request connnection to server for each thread
        connectThreadsToServer();

        // Start game
        startGame();
    }

    public static void main(String[] args) {
        // TODO: allow for args

        new Client();
    }
}
