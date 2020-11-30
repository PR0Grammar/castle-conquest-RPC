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


    public void msg(String m){
        System.out.println("[Client]: " + m);
    }

    public int getTotalCastleDefense(){
        int sum = 0;
        for(Defender d: defenders){
            sum += d.getDefendValue();
        }
        return sum;
    }

    public void startGame(){
        for(Attacker a: attackers){
            a.start();
        }

        for(Defender d: defenders){
            d.start();
        }

        king.start();
    }

    public Client(){
        msg("has started.");
        attackerCount = DEFAULT_ATTACKER_COUNT;
        defenderCount = DEFAULT_DEFENDER_COUNT;
        gateCount = DEFAULT_GATE_COUNT;
        space = DEFAULT_SPACE;
    }

    public Client(int a, int d, int g, int s){
        msg("has started.");
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
                c.attackers.add(new Attacker(new Socket(host, port), i));
            }

            // spawn defenders
            for(int i = 0; i < c.defenderCount; i++){
                c.defenders.add(new Defender(new Socket(host, port), i));
            }
            // spawn king
            c.king = (new King(new Socket(host, port)));

        }
        catch(Exception e){
            System.out.println("Client error: " + e);
        }

        //Start game
        c.startGame();
    }
}
