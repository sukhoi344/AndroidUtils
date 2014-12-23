package chau.networkutil.crypt;

/**
 * Created by chauthai on 12/23/14.
 */
public interface Decryptor {
    public byte[] decrypt(byte[] encryptedData) throws DecryptException;
    public String decrypt(String encryptedText) throws DecryptException;
}
