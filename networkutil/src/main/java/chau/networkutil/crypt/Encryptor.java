package chau.networkutil.crypt;

/**
 * Created by chauthai on 12/23/14.
 */
public interface Encryptor {
    public byte[] encrypt(byte[] data) throws EncryptException;
    public String encrypt(String text) throws EncryptException;
}
