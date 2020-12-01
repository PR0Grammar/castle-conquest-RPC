package shared;

// Shared class between client and server to allow for consistent mapping of
// integers for method invocation

public class RPCMethods {
    public static final int END_CONNECTION = 0;
    public static final int GRAB_WEAPON = 1;
    public static final int ATTACK_GATE = 2;
    public static final int DEFEND_GATE = 3;
    public static final int REST = 4;
    public static final int WAIT_FOR_ESCAPE_GATE = 5;
    public static final int PACK_BELONGINGS = 6;
    public static final int TRY_TO_ESCAPE = 7;


    // Map integer to method name so we can print which method is requested/invoked
    public static String getMethodName(int method){
        switch(method){
            case(END_CONNECTION):
                return "endConnection()";
            case(GRAB_WEAPON):
                return "grabWeapon()";
            case(ATTACK_GATE):
                return "attackGate()";
            case(DEFEND_GATE):
                return "defendGate()";
            case(REST):
                return "rest()";
            case(WAIT_FOR_ESCAPE_GATE):
                return "waitForEscapeGate()";
            case(PACK_BELONGINGS):
                return "packBelongings()";
            case(TRY_TO_ESCAPE):
                return "tryToEscape()";
            default:
                return "";
        }
    }
}
