
In the root project directory:

1. run [javac client/Client.java]
2. run [javac server/Server.java]
3. run [java server.Server] in one window
4. run [java client.Client <int (number of attackers)> <int (number of defenders)> <int (number of gates)> <int (num of spaces)>] OR [java client.Client] in another window
    ex) 
        java client.Client 10 10 2 3
        java client.Client
5. after execution finishes on client, terminate server program (crtl + c)
