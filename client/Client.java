package client;

import java.net.Socket;

public class Client {
    private static int DEFAULT_ATTACKER_COUNT = 10;
    private static int DEFAULT_DEFENDER_COUNT = 10;
    private static int DEFAULT_GATE_COUNT = 2;
    private static int DEFAULT_SPACE = 3;
    private static int port = 3000;
    private static String host = "localhost";

    public int attackerCount;
    public int defenderCount;
    public int gateCount;
    public int space;

    public Client(){
        attackerCount = DEFAULT_ATTACKER_COUNT;
        defenderCount = DEFAULT_DEFENDER_COUNT;
        gateCount = DEFAULT_GATE_COUNT;
        space = DEFAULT_SPACE;
    }

    public Client(int a, int d, int g, int s){
        attackerCount = a;
        defenderCount = d;
        gateCount = g;
        space = s;
    }

    public static void main(String[] args){
        // TODO: allow for args
        Client c = new Client();

        try{
            // spawn attackers
            for(int i = 0; i < c.attackerCount; i++){
                (new Attacker(new Socket(host, port), i)).start();
            }

            // spawn defenders
            for(int i = 0; i < c.defenderCount; i++){
                (new Defender(new Socket(host, port), i)).start();
            }
            // spawn king
            (new King(new Socket(host, port))).start();

        }
        catch(Exception e){
            System.out.println("Client error: " + e);
        }
    }

}
