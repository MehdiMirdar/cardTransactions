package ir.dotin.card_transactions.config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

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
@Service
public class Configuration {

    /**
     * <p>this method will return the hashed form of requested password
     * </p>
     *
     * @param pass is amount of requested password
     * @return this method will return the hashStr(hashed form of password)
     * @since 1.0
     */
    public String hashPassword(String pass) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte hashBytes[] = messageDigest.digest(pass.getBytes(StandardCharsets.UTF_8));
        BigInteger noHash = new BigInteger(1, hashBytes);
        String hashStr = noHash.toString(16);
        return hashStr;
    }

    /**
     * <p>this method will parse string to json
     * </p>
     *
     * @param str is amount of client request
     * @return this method will return boolean value after check the destinationCardNumber
     * @since 1.0
     */
    public JSONObject strToJson(String str) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(str);
    }
}
