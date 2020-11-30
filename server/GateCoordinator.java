package server;

import java.util.ArrayList;

import client.Client;

// Monitor class to allow for mutual exclusion over Gate assingnment
// All operations related to Gate selection and waiting should be done through this monitor

public class GateCoordinator {
    private ArrayList<Gate> gates;
    private ArrayList<Object> waitingAttackers;
    private ArrayList<Object> waitingDefenders;
    private boolean attackerIsGrabbing;
    private boolean defenderIsGrabbing;

    public GateCoordinator(int numOfGates, int spacePerGate){
        gates = new ArrayList<>();
        waitingAttackers = new ArrayList<>();
        waitingDefenders = new ArrayList<>();
        attackerIsGrabbing = false;
        defenderIsGrabbing = false;

        // Create the gates
        for(int i = 0; i < numOfGates; i++){
            gates.add(new Gate(this, spacePerGate, i));
        }
    }

    public synchronized void platoonAllowNextAttacker(){
        // If others are waiting to go, and there is a gate available to attack
        // signal the next waiting attacker to go.

        if(waitingAttackers.size() > 0 && gateAvailableForAttacker()){
            Object o = waitingAttackers.remove(0);
            synchronized(o){
                o.notify();
            }
        }
        // Otherwise change flag so incoming attackers don't necessarily have to wait
        else{
            attackerIsGrabbing = false;
        }
    }

    public synchronized void platoonAllowNextDefender(){
        // If others are waiting to go, and there is a gate available to defend
        // signal the next waiting defender to go.

        if(waitingDefenders.size() > 0 && gateAvailableForDefender()){
            Object o = waitingDefenders.remove(0);
            synchronized(o){
                o.notify();
            }
        }
        // Otherwise change flag so incoming defenders don't necessarily have to wait
        else{
            defenderIsGrabbing = false;
        }
    }

    public synchronized Gate getGateToAttack(ClientHelper c, String attackerName){
        Gate leastAttacked = null;
        int numOfAttackersAtGate = Integer.MAX_VALUE;

        for(Gate g: gates){
            int numOfA = g.attackerCount();
            if(!g.isBattleReady() && g.canTakeMoreAttackers() && numOfA < numOfAttackersAtGate){
                leastAttacked = g;
                numOfAttackersAtGate = numOfA;
            }
        }

        leastAttacked.assignAttacker();
        c.msg(attackerName + " has been assigned to attack " + leastAttacked.getTitle() + ". They are headed there now");
        return leastAttacked;
    }

    public synchronized Gate getGateToDefend(ClientHelper c, String defenderName){
        Gate leastDefended = null;
        int numOfDefendersAtGate = Integer.MAX_VALUE;

        for(Gate g: gates){
            int numOfD = g.defenderCount();
            if(!g.isBattleReady() && g.canTakeMoreDefenders() && numOfD < numOfDefendersAtGate){
                numOfDefendersAtGate = numOfD;
                leastDefended = g;
            }
        }

        leastDefended.assignDefender();
        c.msg(defenderName + " has been assigned to defend " + leastDefended.getTitle() + ". They are headed there now");
        return leastDefended;
    }

    public synchronized void signalWaitingAttackersAfterBattle(){
        // NOTE: we only signal is an attacker is not grabbing, since if there is,
        // they'll signal using platoon policy

        if(waitingAttackers.size() > 0 && gateAvailableForAttacker() && !attackerIsGrabbing){
            Object o = waitingAttackers.remove(0);
            synchronized(o){o.notify();}
        }
    }

    public synchronized void signalWaitingDefendersAfterBattle(){
        // NOTE: we only signal is an defender is not grabbing, since if there is,
        // they'll signal using platoon policy
        if(waitingDefenders.size() > 0 && gateAvailableForDefender() && !defenderIsGrabbing){
            Object o = waitingDefenders.remove(0);
            synchronized(o){
                o.notify();
            }
        }
    }

    // Last thread leaving a Gate calls this meethod to free up Gate and allow for waiting threads to go
    public synchronized void afterBattleEnds(Gate g){
        // Free up Gate
        g.setIsBattleReady(false);
        g.unassignAttackers();
        g.unassignDefenders();

        // Let waiting threads know they can go to this Gate (FIFO order)
        signalWaitingAttackersAfterBattle();
        signalWaitingDefendersAfterBattle();
    }

    private synchronized boolean gateAvailableForAttacker(){
        boolean hasGate = false;

        for(Gate g: gates){
            if(!g.isBattleReady() && g.canTakeMoreAttackers()){
                hasGate = true;
            }
        }

        return hasGate;
    }

    private synchronized boolean gateAvailableForDefender(){
        boolean hasGate = false;
        
        for(Gate g : gates){
            if(!g.isBattleReady() && g.canTakeMoreDefenders()){
                hasGate = true;
            }
        }
        return hasGate;
    }

    private synchronized boolean attackerCanGrab(Object convey, ClientHelper a, String attackerName){
        if(!attackerIsGrabbing &&  waitingAttackers.size() == 0 && gateAvailableForAttacker()){
            attackerIsGrabbing = true;
            a.msg(attackerName + " is grabbing a gate.");

            return true;
        }
        else{
            a.msg(attackerName + " has to wait for a gate since others ahead/grabbing, or no gate available");
            waitingAttackers.add(convey);
            return false;
        }
    }

    private synchronized boolean defenderCanGrab(Object convey, ClientHelper d, String defenderName){
        if(!defenderIsGrabbing && waitingDefenders.size() == 0 && gateAvailableForDefender()){
            defenderIsGrabbing = true;
            d.msg(defenderName + " is grabbing a gate.");

            return true;
        }
        else{
            d.msg(defenderName + " has to wait for a gate since others ahead/grabbing, or no gate available");
            waitingDefenders.add(convey);
            return false;
        }
    }
    
    public void attackerWaitForGate(ClientHelper a, String attackerName){
        Object convey = new Object();

        synchronized(convey){
            if(!attackerCanGrab(convey, a, attackerName)){
                while(true){
                    try{
                        convey.wait(); 
                        break;
                    }catch(Exception e){}
                }
            }
        }
    }

    public void defenderWaitForGate(ClientHelper d, String defenderName){
        Object convey = new Object();

        synchronized(convey){
            if(!defenderCanGrab(convey, d, defenderName)){
                while(true){
                    try{
                        convey.wait(); 
                        break;
                    }
                    catch(Exception e){}
                }
            }
        }
    }
}
