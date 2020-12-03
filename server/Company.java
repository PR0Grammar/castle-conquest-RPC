package server;

import java.util.ArrayList;

public class Company extends Thread {
    private static String name = "Company";
    public static long time = System.currentTimeMillis();

    private int sumOfAttackers;
    private int sumOfDefenders;
    private GameStatus gameStatus;
    private ArrayList<String> attackers;
    private ArrayList<String> defenders;
    private Gate gate;
    private Castle castle;
    private EscapeRoutes escapeRoutes;

    public Company(Castle c, 
                   ArrayList<String> attackers, 
                   ArrayList<String> defenders, 
                   Gate g, GameStatus 
                   gameStatus, 
                   int sumOfAttackers, 
                   int sumOfDefenders,
                   EscapeRoutes er
                   ) {
        this.gameStatus = gameStatus;
        this.sumOfAttackers = sumOfAttackers;
        this.sumOfDefenders = sumOfDefenders;
        this.attackers = attackers;
        this.defenders = defenders;
        this.gate = g;
        this.castle = c;
        escapeRoutes = er;

        setName(name + '-' + g.getTitle() + '-' + System.currentTimeMillis());
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    private void determineWinner() {
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            return;
        }
        
        msg("Total Attacker Value: " + sumOfAttackers + " vs. Total Defender Value: " + sumOfDefenders);
        
        if (sumOfAttackers > sumOfDefenders) {
            msg("Attackers win the battle! Castle will take " + 
                sumOfAttackers + 
                " damage. Castle health is now " + 
                castle.takeDamage(sumOfAttackers));
            
            if(castle.getHealth() <= 0){
                gameStatus.setStatus(GameStatus.ATTACKERS_WIN);
            }
        } else if (sumOfAttackers < sumOfDefenders) {
            msg("Defenders win the battle! We will let the King know " + gate.getTitle() + " is open to escape.");
            escapeRoutes.addEscapeRoute(gate);
        } else {
            msg("It's a tie! Nothing will take place.");
        }
    }

    private void simulateBattleTime() {
        // Simulate battle time (sleep between 4000-5000 ms)
        try {
            Thread.sleep(((int) Math.ceil(Math.random() * 1000) + 4000));
        } catch (Exception e) {
        }
    }

    private void notifyThreadsWaitingOnResult(){
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            return;
        }
        
        msg("Notifying all threads in this company for battle results");
        synchronized(gate){
            gate.notifyAll();
        }
    }

    public void run() {
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET){
            return;
        }
        
        msg("BATTLE HAS BEGUN!!!\n     Attackers: " + String.join(",", attackers) + "\n     Defenders: " + String.join(",", defenders));

        simulateBattleTime();
        determineWinner();
        notifyThreadsWaitingOnResult();

        msg("is terminating.");
    }
}
