package server;

public class GameStatus {
    public static final int NO_WINNER_YET = 0;
    public static final int DEFENDERS_WIN = 1;
    public static final int ATTACKERS_WIN = 2;

    private int status = NO_WINNER_YET;


    public synchronized int getGameStatus(){
        return status;
    }

    // Can only be set once
    public synchronized void setStatus(int s){
        if(status != NO_WINNER_YET){
            if(s == DEFENDERS_WIN){
                System.out.println("+++++");
                System.out.println("+++++");
                System.out.println("+++++");
                System.out.println("DEFENDERS WIN!");
                System.out.println("+++++");
                System.out.println("+++++");
                System.out.println("+++++");
            }
            else if(s == ATTACKERS_WIN){
                System.out.println("+++++");
                System.out.println("+++++");
                System.out.println("+++++");
                System.out.println("ATTACKERS WIN!");
                System.out.println("+++++");
                System.out.println("+++++");
                System.out.println("+++++");
            }
            status = s;
        }
    }
}
