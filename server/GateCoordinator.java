package server;

import java.util.ArrayList;

// Monitor class to allow for mutual exclusion over Gate assingnment
// All operations related to Gate selection and waiting should be done through this monitor

public class GateCoordinator {
    private ArrayList<Gate> gates;
    private final int spacePerGate;
    private ArrayList<Object> waitingAttackers;
    private ArrayList<Object> waitingDefenders;

    public GateCoordinator(ArrayList<Gate> g, int spacePerGate){
        gates = g;
        waitingAttackers = new ArrayList<>();
        waitingDefenders = new ArrayList<>();
        this.spacePerGate = spacePerGate;
    }

    private synchronized void addWaitingAttacker(Object convey){
        waitingAttackers.add(convey);
    }

    public synchronized void removeWaitingAttacker(Object convey){
        waitingAttackers.remove(convey);
    }

    public synchronized void addWaitingDefender(Object convey){
        waitingDefenders.add(convey);
    }

    public synchronized void removeWaitingDefender(Object convey){
        waitingDefenders.remove(convey);
    }


    private synchronized Gate getLeastAttackedGate(Object convey){
        int waitingIndexOfAttacker = waitingAttackers.indexOf(convey);

        // If others are waiting and this thread is not the next to go, return null
        if(waitingAttackers.size() > 0 && waitingIndexOfAttacker != 0){
            // If this thread is not already in the queue, add it
            if(waitingIndexOfAttacker == -1){
                waitingAttackers.add(convey);
            }

            return null;
        }

        int leastAttackerCount = Integer.MAX_VALUE;
        Gate leastAttacked = null;

        for(Gate g: gates){
            int attackerCount = g.attackerCount();

            if(g.canTakeMoreAttackers() && attackerCount < leastAttackerCount){
                leastAttackerCount = attackerCount;
                leastAttacked = g;
            }
        }

        // If no Gate available and thread is not already in waiting queue
        if(leastAttacked == null && waitingIndexOfAttacker == -1){
            addWaitingAttacker(convey);
        }
        
        // If this thread was the one from waitingDefender, remove it since it got a Gate
        if(leastAttacked != null && waitingIndexOfAttacker == 0){
            removeWaitingAttacker(convey);
        }

        // If Gate is now full, set isBattleReady=T to prevent assignment to this Gate
        if(leastAttacked != null && leastAttacked.isFull()){
            leastAttacked.setIsBattleReady(true);
        }

        return leastAttacked;
    }

    private synchronized Gate getLeastDefendedGate(Object convey){
        int waitingIndexOfDefender = waitingDefenders.indexOf(convey);

        // If others are waiting and this thread is not the next to go, return null
        if(waitingDefenders.size() > 0 && waitingIndexOfDefender != 0){
            // If this thread is not already in the queue, add it
            if(waitingIndexOfDefender == -1){
                addWaitingDefender(convey);
            }
            
            return null;
        }

        int leastDefendedCount = Integer.MAX_VALUE;
        Gate leastDefended = null;

        for(Gate g: gates){
            int defCount = g.defenderCount();

            if(g.canTakeMoreDefenders() && defCount < leastDefendedCount){
                leastDefendedCount = defCount;
                leastDefended = g;
            }
        }

        // If no Gate available and thread is not already in waiting queue
        if(leastDefended == null && waitingIndexOfDefender == -1){
            addWaitingDefender(convey);
        }
        
        // If this thread was the one from waitingDefender, remove it since it got a Gate
        if(leastDefended != null && waitingIndexOfDefender == 0){
            removeWaitingDefender(convey);
        }

        // If Gate is now full, set isBattleReady=T to prevent assignment to this Gate
        if(leastDefended != null && leastDefended.isFull()){
            leastDefended.setIsBattleReady(true);
        }

        return leastDefended;
    }

    public synchronized void signalNextWaitingAttacker(){
        if(waitingAttackers.size() > 0){
            synchronized(waitingAttackers.get(0)){
                waitingAttackers.get(0).notify();
            }
        }
    }

    public synchronized void signalNextWaitingDefender(){
        if(waitingDefenders.size() > 0){
            synchronized(waitingDefenders.get(0)){
                waitingDefenders.get(0).notify();
            }
        }
    }

    public synchronized void afterBattleEnds(Gate g){
        // Free up Gate
        g.setIsBattleReady(false);
        g.unassignAttackers();
        g.unassignDefenders();

        // Let waiting threads know they can go to this Gate (FIFO order)
        signalNextWaitingAttacker();
        signalNextWaitingDefender();
    }

    
    public Gate findGateToAttack(ClientHelper a){
        Object convey = new Object();

        synchronized(convey){
            Gate leastAttackedGate = getLeastAttackedGate(convey);
            
            while(leastAttackedGate == null){
                try{convey.wait();}
                catch(Exception e){}

                leastAttackedGate = getLeastAttackedGate(convey);
            }

            // Platoon policy: signal next waiting attacker to try
            signalNextWaitingAttacker();

            return leastAttackedGate;
        }
    }

    public Gate findGateToDefend(ClientHelper d){
        Object convey = new Object();

        synchronized(convey){
            // Try and get a gate
            Gate leastDefendedGate = getLeastDefendedGate(convey);

            // If unsuccessful, wait to be signaled
            while(leastDefendedGate == null){
                try{convey.wait();}
                catch(Exception e){}
                // Try again
                leastDefendedGate = getLeastDefendedGate(convey);
            }

            // Platoon policy: signal next waiting defender to try
            signalNextWaitingDefender();
            return leastDefendedGate;
        }
    }
}
