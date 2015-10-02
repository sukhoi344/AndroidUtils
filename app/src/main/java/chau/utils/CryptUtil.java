package chau.utils;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * This class provides simple String decrypt and encrypt methods.
 * Created by chauthai on 12/18/14.
 */
public class CryptUtil {

    /** Default salt */
    private static byte[] SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    /** Default password */
    private static char[] PASSWORD = {
            'j', 'a', 'n', 'e', '+', 'A'
    };

    /**
     * Set salt for decryption and encryption
     * @param salt array of bytes
     */
    public static void setSalt(byte[] salt) {
        if (salt != null && salt.length != 0)
            SALT = salt;
    }

    /**
     * Set password for encryption and decryption
     * @param password array of bytes
     */
    public static void setPassword(char[] password) {
        if (password != null && password.length != 0)
            PASSWORD = password;
    }

    /**
     * Encrypt String object with the default salt and password
     * @param property String to be encrypted
     * @return encrypted string
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(@NonNull String property) throws GeneralSecurityException, UnsupportedEncodingException {
        return base64Encode(getCipher(Cipher.ENCRYPT_MODE).doFinal(property.getBytes("UTF-8")));
    }

    /**
     * Encrypt String object
     * @param property String to be encrypted
     * @param salt for obfuscation
     * @param password to decrypt later
     * @return encrypted String
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(@NonNull String property, @NonNull String salt, @NonNull String password)
            throws GeneralSecurityException, UnsupportedEncodingException {
        final byte[] saltBytes = salt.getBytes("UTF-8");
        final char[] passChars = password.toCharArray();

        return base64Encode(getCipher(Cipher.ENCRYPT_MODE, saltBytes, passChars)
                .doFinal(property.getBytes("UTF-8")));
    }

    /**
     * Decrypt encrypted String object with the default salt and password
     * @param property String to be decrypted
     * @return decrypted string
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String decrypt(@NonNull String property) throws GeneralSecurityException, IOException {
        return new String(getCipher(Cipher.DECRYPT_MODE).doFinal(base64Decode(property)), "UTF-8");
    }

    /**
     * Decrypt encrypted String object
     * @param property String to be decrypted
     * @param salt used for obfuscation
     * @param password used to encrypt
     * @return decrypted String
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String decrypt(@NonNull String property, @NonNull String salt, @NonNull String password)
            throws GeneralSecurityException, IOException {
        final byte[] saltBytes = salt.getBytes("UTF-8");
        final char[] passChars = password.toCharArray();

        return new String(getCipher(Cipher.DECRYPT_MODE, saltBytes, passChars)
                .doFinal(base64Decode(property)), "UTF-8");
    }

    private static Cipher getCipher(int mode, byte[] salt, char[] password) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException {

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(mode, key, new PBEParameterSpec(salt, 20));

        return pbeCipher;
    }

    private static Cipher getCipher(int mode) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException {
        return getCipher(mode, SALT, PASSWORD);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.decode(property, Base64.DEFAULT);
    }
}