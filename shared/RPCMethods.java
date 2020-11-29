package shared;

// Shared class between client and server to allow for consistent mapping of
// integers for method invocation

public class RPCMethods {
    public static final int END_CONNECTION = 0;
    public static final int GRAB_WEAPON = 1;
    public static final int ATTACK_GATE = 2;
    public static final int DEFEND_GATE = 3;
    public static final int REST = 4;


    // Map integer to method name on the server side
    public static String getMethodName(int method){
        switch(method){
            case(END_CONNECTION):
                return "endConnection()";
            case(GRAB_WEAPON):
                return "grabWeapon()";
            default:
                return "";
        }
    }
}
