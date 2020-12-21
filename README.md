# Castle Conquest: Attackers vs. Defenders

## About

The villagers are tired of the King imposing higher taxes and decided to revolt. The villagers (known as the Attackers) will grab weapons from the Armory in FIFO order, then march to the least attacked gate they can find. The King's Defenders likewise will choose to protect the least defended gate. Once an even number of Attackers and Defenders have arrived at a gate (depending on available space at a gate), the battle will begin. Any Defender or Attacker not assigned to a gate will wait for a free gate once a battle is over. 

If Attackers win the battle at a gate, the Castle will take damage. If the Defenders win, the King will attempt to escape from that gate once the King has finished packing their belongings. If another Attacker arrives to that gate before the King can escape, the King must return to the castle and try again. The winner is determined by which group has the greater sum of attack points or defend points.

All Attackers and Defenders operate in FIFO order when choosing a gate. After a battle, Attackers and Defenders must rest up a bit before returning to battle. Gates don't have health, so they are reused for battle grounds. Attackers and Defenders also don't have health, so they can join multiple battles.

The Defenders win if the King can successfully escape. The Attackers win if the Castle health reaches <= 0.  

The Attacker, Defender and King threads are on the client side and must issue RPC to the server to helper threads to perform different actions (get a gate, attack, rest, etc.).

## Synchronzation methods:

- RPC betwen client/server on localhost
- Thread synchronization on server using Java monitors (armory, gates, etc)

## To run:

In the root project directory:

1. run [javac client/Client.java]
2. run [javac server/Server.java]
3. run [java server.Server] in one window
4. run [java client.Client <int (number of attackers)> <int (number of defenders)> <int (number of gates)> <int (num of spaces)>] OR [java client.Client] in another window
    ex) 
        java client.Client 10 10 2 3
        java client.Client
5. after execution finishes on client, terminate server program (crtl + c)

