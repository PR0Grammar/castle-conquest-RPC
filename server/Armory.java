package server;

import java.util.ArrayList;


public class Armory {
    private ArrayList<ClientHelper> waitingAttackers;
    private boolean isOccupied;
    private ClientHelper nextToEnter;
    private GameStatus gameStatus;

    public Armory(GameStatus gs) {
        waitingAttackers = new ArrayList<>();
        isOccupied = false;
        gameStatus = gs;
        nextToEnter = null;
    }

    // Enter armory in FIFO order
    // If occupied or others ahead waiting, then thread must wait, otherwise it can enter armory
    public synchronized void enterArmory(ClientHelper c, String clientThreadName) {
        if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET) return;
        
        if (isOccupied || waitingAttackers.size() > 0) {
            // HAHA c.msg(clientThreadName + " is waiting to grab a weapon since armory full or others ahead.");
            waitingAttackers.add(c);

            while (nextToEnter == null || !nextToEnter.equals(c)) {
                try {
                    wait();
                } catch (Exception e) {
                }

                if(gameStatus.getGameStatus() != GameStatus.NO_WINNER_YET) return;
            }
        }

        isOccupied = true;
        // HAHA c.msg(clientThreadName + " has entered the armory.");
    }

    // Weapon of random value 1-10
    public int getWeapon() {
        return (int) Math.ceil(Math.random() * 10);
    }

    // Leave armory, set next waiting attacker to go if one exists and signal,
    // otherwise set isOccupied=false, nextToEnter=null
    public synchronized void leaveArmory(ClientHelper c, String clientThreadName) {
        // HAHA c.msg(clientThreadName + " has exited the armory.");

        if (waitingAttackers.size() > 0) {
            nextToEnter = waitingAttackers.remove(0);
            notifyAll();
        } else {
            isOccupied = false;
            nextToEnter = null;
        }
    }
}
