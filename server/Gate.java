package server;

import java.util.ArrayList;

// NOTE: There are variables that are mutually exclusive from one another
// Some are only accessed by GateCoordinator and the others are only accessed by
// the threads in assignedAttackers/assignedDefenders. This allows for assignment to Gate to be mutually
// exclusive from the battle.

public class Gate {
    public final static String name = "Gate";
    public final int space;
    public final int id;
    private final GateCoordinator gateCoordinator;

    // Variables used by GateCoordinator. These variables allow for GateCoordinator
    // to assign attacker and defenders and let GateCoordinator know 
    // when this Gate should be ignored (isBattleReady).
    // Only access/modify using GateCoorindator
    private int assignedAttackers;
    private int assignedDefenders;
    private boolean isBattleReady;

    // Variables used only by threads who are assigned to this gate.
    // They should be accessed only by synchronized methods in this class
    private int arrivedAttackers;
    private int arrivedDefenders;

    public Gate(GateCoordinator gc, int space, int id){
        this.id = id;
        assignedAttackers = new ArrayList<>();
        assignedDefenders = new ArrayList<>();
        arrivedAttackers = 0;
        arrivedDefenders = 0;
        
        isBattleReady = false;
        this.space = space;
        gateCoordinator = gc;
    }

    public String getTitle(){
        return name + '-' + id;
    }

    // All GateCoordintaor methods
    public int defenderCount(){
        return assignedDefenders;
    }

    public int attackerCount(){
        return assignedAttackers;
    }

    public boolean canTakeMoreAttackers(){
        return !isBattleReady && assignedAttackers < space;
    }

    public boolean canTakeMoreDefenders(){
        return !isBattleReady && assignedDefenders < space;
    }

    public boolean isFull(){
        return assignedAttackers == space && assignedDefenders == space; 
    }

    public void assignAttacker(String attackerName){
        assignedAttackers++;
    }

    public void assignDefender(String defenderName){
        assignedDefenders++;
    }

    public void unassignAttackers(){
        assignedAttackers = 0;
    }

    public void unassignDefenders(){
        assignedDefenders = 0;
    }

    public void setIsBattleReady(boolean v){
        isBattleReady = v;
    }

    // Prevents GateCoordinator from using this Gate until the battle results are in
    public boolean isBattleReady(){
        return isBattleReady;
    }

    // All synchornized methods. ONLY ASSIGNED THREADS(attacker + defenders) ALLOWED TO USE.
    public synchronized boolean battleCanBegin(){
        return arrivedAttackers == space && arrivedDefenders == space;
    }

    public synchronized void sumUpAtackersDefendersValues(){
        // TODO;
        System.out.println("SUM UP A D VALUES!");
    }

    public synchronized void defenderLeaveGate(ClientHelper a, String defenderName) {
        arrivedDefenders--;

        // If last thread to leave, free up Gate and let waiting threads go
        if(arrivedAttackers + arrivedDefenders == 0){
            gateCoordinator.afterBattleEnds(this);
        }
    }

    public synchronized void attackerLeaveGate(ClientHelper a, String attackerName){
        arrivedAttackers--;
        
        // If last thread to leave, free up Gate and let waiting threads go
        if(arrivedAttackers + arrivedDefenders == 0){
            gateCoordinator.afterBattleEnds(this);
        }
    }


    public synchronized void attack(ClientHelper c, String attackerName){
        arrivedAttackers++;

        c.msg(attackerName + " has arrived to " + getTitle());

        if(battleCanBegin()){
            c.msg(attackerName + " is last last to arrive to " + getTitle() + ". The battle can begin now. They will notify everyone.");
            sumUpAtackersDefendersValues();
        }
        else{
            c.msg(attackerName + " has to wait all others to arrive.");
            try{wait();}catch(Exception e){}
            // TODO: wait for battle results
        }
    }

    public synchronized void defend(ClientHelper c, String defenderName){
        arrivedDefenders++;

        c.msg(defenderName + " has arrived to " + getTitle());

        if(battleCanBegin()){
            c.msg(defenderName + " is last last to arrive to " + getTitle() + ". The battle can begin now. They will notify everyone.");
            notifyAll(); // Last thread notifies everyone waiting at this Gate
            sumUpAtackersDefendersValues(); // Last thread also calculates battle results
        }
        else{
            c.msg(defenderName + " has to wait all others to arrive.");
            try{wait();}catch(Exception e){}
            // TODO: wait for battle results
        }
    }
}
