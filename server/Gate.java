package server;

import java.util.ArrayList;

public class Gate {
    // Variables used by GateCoordinator. These variables allow for GateCoordinator
    // to assign attacker and defenders and let GateCoordinator know 
    // when this Gate should be ignored (isBattleReady).
    // Only access/modify using GateCoorindator
    private ArrayList<String> assignedAttackers;
    private ArrayList<String> assignedDefenders;
    private boolean isBattleReady;

    // Variables used only by threads who are assigned to this gate.
    // They should be accessed only by synchronized methods in this class

    public Gate(){
        assignedAttackers = new ArrayList<>();
        assignedDefenders = new ArrayList<>();
        isBattleReady = false;
    }

    // All GateCoordintaor methods
    public int getAttackerCount(){
        return assignedAttackers.size();
    }

    public int getDefenderCount(){
        return assignedDefenders.size();
    }

    public void assignAttacker(){
        // Add attacker, if gate is now full, start battle
    }

    public void assignDefender(){
        // Add defender, if gate is now full, start battle
    }

    public void setIsBattleReady(boolean v){
        isBattleReady = v;
    }

    // Synchornized methods. ONLY ASSIGNED THREADS ALLOWED TO USE.

}
