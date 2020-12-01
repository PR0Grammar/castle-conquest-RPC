package server;

public class GameStatus {
    public static final int NO_WINNER_YET = 0;
    public static final int DEFENDERS_WIN = 1;
    public static final int ATTACKERS_WIN = 2;

    private int status = NO_WINNER_YET;


    public synchronized int getGameStatus(){
        return status;
    }

    public synchronized void setStatus(int s){
        status = s;
    }
}
