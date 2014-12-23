package chau.networkutil.crypt;

/**
 * Created by chauthai on 12/23/14.
 */
public class EncryptException extends Exception {
    public EncryptException() {
        super("Cannot encrypt message");
    }

    public EncryptException(String detailMessage) {
        super(detailMessage);
    }
}
