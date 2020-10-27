package ir.dotin.card_transactions.config;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * this class is config some features
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-26
 */


public class Configuration {

    /**
     * <p>this method will return the hashed form of requested password
     * </p>
     *
     * @param pass is amount of requested password
     * @return this method will return the hashStr(hashed form of password)
     * @since 1.0
     */
    public static String passwordHash(String pass) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte hashBytes[] = messageDigest.digest(pass.getBytes(StandardCharsets.UTF_8));
        BigInteger noHash = new BigInteger(1, hashBytes);
        String hashStr = noHash.toString(16);
        return hashStr;
    }
}
