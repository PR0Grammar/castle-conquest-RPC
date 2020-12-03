package server;

public class Castle {
    private int health;

    public Castle(int health){
        this.health = health;
    }

    public synchronized int takeDamage(int dmgAmount){
        health -= dmgAmount;
        return health;
    }

    public synchronized int getHealth(){
        return health;
    }
}
