package server;

public class Belongings {
    private static final int PACKING_TIME = 3000;

    // Pack belongings must be factor less than attacker wait time (at least 1/2)
    public synchronized void pack(){
        try{
            wait(PACKING_TIME);
        }
        catch(Exception e){}
    }
}
