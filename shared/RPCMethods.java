package shared;

// Shared class between client and server to allow for consistent mapping of
// integers for method invocation

public class RPCMethods {
    public static final int END_CONNECTION = 0;
    public static final int GRAB_WEAPON = 1;

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
