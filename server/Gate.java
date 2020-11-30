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
    private ArrayList<String> arrivedAttackers;
    private ArrayList<String>  arrivedDefenders;

    public Gate(GateCoordinator gc, int space, int id){
        this.id = id;
        assignedAttackers = 0;
        assignedDefenders = 0;

        arrivedAttackers = new ArrayList<>();
        arrivedDefenders = new ArrayList<>();
        
        isBattleReady = false;
        this.space = space;
        gateCoordinator = gc;
    }

    public String getTitle(){
        return name + '-' + id;
    }

    //**** All GateCoordintaor methods
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

    public void assignAttacker(){
        assignedAttackers++;
    }

    public void assignDefender(){
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

    //**** All synchornized methods. ONLY ASSIGNED THREADS(attacker + defenders) ALLOWED TO USE.
    
    // Can begin once all threads arrived
    public synchronized boolean battleCanBegin(){
        return arrivedAttackers.size() == space && arrivedDefenders.size() == space;
    }

    // Last thread to arrive executes this
    public synchronized void sumUpAtackersDefendersValues(){
        // TODO
        String attackers = String.join(", ", arrivedAttackers);
        String defenders = String.join(", ", arrivedDefenders);

        System.out.println("Battle can begin at " + getTitle() + " with: " + attackers + " " + defenders);
        
        try{Thread.sleep(5000);}catch(Exception e){}

        System.out.println("SUM UP A D VALUES!");
    }

    // After battle, we leave the gate
    public synchronized void defenderLeaveGate(ClientHelper d, String defenderName) {
        arrivedDefenders.remove(defenderName);

        d.msg(defenderName + " is leaving the " + getTitle());
        // If last thread to leave, free up Gate and let waiting threads go
        if(arrivedAttackers.size() + arrivedDefenders.size() == 0){
            gateCoordinator.afterBattleEnds(this);
        }
    }

    // After battle, we leave the gate
    public synchronized void attackerLeaveGate(ClientHelper a, String attackerName){
        arrivedAttackers.remove(attackerName);
        
        
        a.msg(attackerName + " is leaving the " + getTitle());

        // If last thread to leave, free up Gate and let waiting threads go
        if(arrivedAttackers.size() + arrivedDefenders.size() == 0){
            gateCoordinator.afterBattleEnds(this);
        }
    }

    public synchronized void attack(ClientHelper c, String attackerName){
        arrivedAttackers.add(attackerName);

        c.msg(attackerName + " has arrived to " + getTitle());

        if(battleCanBegin()){
            c.msg(attackerName + " is last last to arrive to " + getTitle() + ". The battle can begin now. They will notify everyone.");
            notifyAll(); // Last thread notifies everyone waiting at this Gate
            sumUpAtackersDefendersValues();
        }
        else{
            c.msg(attackerName + " has to wait all others to arrive.");
            try{wait();}catch(Exception e){}
            // TODO: wait for battle results
        }
    }

    public synchronized void defend(ClientHelper c, String defenderName){
        arrivedDefenders.add(defenderName);

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
