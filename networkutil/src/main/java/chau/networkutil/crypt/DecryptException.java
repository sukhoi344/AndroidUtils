package chau.networkutil.crypt;

/**
 * Created by chauthai on 12/23/14.
 */
public class DecryptException extends Exception {
    public DecryptException() {
        super("Cannot decrypt message");
    }

    public DecryptException(String detailMessage) {
        super(detailMessage);
    }
}
