package ir.dotin.card_transactions.config;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Configuration {

    public static String passwordHash(String pass) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte hashBytes[] = messageDigest.digest(pass.getBytes(StandardCharsets.UTF_8));
        BigInteger noHash = new BigInteger(1, hashBytes);
        String hashStr = noHash.toString(16);
        return hashStr;
    }
}
