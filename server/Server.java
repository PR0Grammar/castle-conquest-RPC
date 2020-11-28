package server;


import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int port = 3000;
    private ServerSocket server;
    private int clientHelperCounter = 0;

    public void msg(String m){
        System.out.println("[Server]: " + m);
    }
    
    public Server(){
        try{
            // Monitors for synchronization
            Armory armory = new Armory();

            // Create server
            server = new ServerSocket(port);
            
            msg("has started. Listening on port " + port);
            
            while(true){
                Socket s = server.accept();
                msg("connected to new client. Creating ClientHelper thread with id = " + clientHelperCounter);
                (new ClientHelper(
                    s, 
                    clientHelperCounter++,
                    armory
                )).start();
            }
        }
        catch(Exception e){
            System.out.println("Server error: " + e);
        }
    }

    public static void main(String[] args){
        new Server();
    }
}
