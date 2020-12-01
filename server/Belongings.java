package server;

public class Belongings {
    private static final int PACKING_TIME = 4000;

    // Pack belongings must be factor less than attacker wait time
    public synchronized void pack(){
        try{
            wait(PACKING_TIME);
        }
        catch(Exception e){}
    }
}
