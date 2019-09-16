package clientserver;

/**
 *
 * @author DEGUZMAN
 */
public final class Constants {

    public static final String GET_ID_REQUEST = "get id";
    public static final String GET_ACCOUNT_REQUEST = "get accounts";
    public static final String VERIFY_PASSWORD_REQUEST = "verify password";
    public static final String GET_ACCOUNT_BALANCES_REQUEST = "get account balances";
    public static final String WITHDRAW_REQUEST = "withdraw";
    
    public static final String ACTION_SUCCESSFUL = "successful";
    public static final String ACTION_UNSUCCESSFUL = "unsuccessful";
    public static final String DONT_EXIST = "dont exist";
    public static final String EXIST = "exist";
    
    public static final String ID_NUMBER = "id_number";
    public static final String ACCOUNT_NUMBER = "account_number";
    
    private Constants() {
        //this prevents even the native class from calling this ctor as well :
        throw new AssertionError();
    }
}
