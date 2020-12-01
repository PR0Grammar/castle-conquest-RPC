package server;

import java.util.ArrayList;

public class EscapeRoutes {
    private ArrayList<Gate> escapeRoutes = new ArrayList<>();
    private Gate currentEscapeRoute = null;
    private GameStatus gameStatus;

    public EscapeRoutes(GameStatus gs){
        this.gameStatus = gs;
    }

    public synchronized void removeEscapeRoute(Gate g){
        if(escapeRoutes.indexOf(g) != -1){
            escapeRoutes.remove(g);
        }
    }

    // Add escape route if Defenders win at a gate and also notify king
    public synchronized void addEscapeRoute(Gate g){
        if(escapeRoutes.indexOf(g) == -1){
            escapeRoutes.add(g);
        }

        // Let the king know a gate opened up.
        notify();
    }

    // Wait to be notified of escape route
    public synchronized void waitForEscapeRoute(ClientHelper c, String kingName){
        // Wait until there is an open gate
        while(escapeRoutes.size() == 0){
            try{
                wait();
            }
            catch(Exception e){}
        }
        
        currentEscapeRoute = escapeRoutes.get(0);
        c.msg(kingName + " has a chance to escape through " + currentEscapeRoute.getTitle() + ".");
    }

    // Try to escape after packing belongings
    public synchronized void tryToEscape(ClientHelper c,  String kingName){
        if(escapeRoutes.indexOf(currentEscapeRoute) != -1){
            // TODO: GameStatus Update
            c.msg(kingName + " has escaped through " + currentEscapeRoute.getTitle() +". DEFENDERS WIN!!!");
        }   
        else{
            c.msg(kingName + " couldn't escape since an Attacker arrived at "+ 
                currentEscapeRoute.getTitle() +
                ". They will wait again...");
        }

        currentEscapeRoute = null;
    }
}
