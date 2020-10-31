package ir.dotin.card_transactions.controller;

import ir.dotin.card_transactions.config.Configuration;
import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.service.CardService;
import ir.dotin.card_transactions.service.TransactionService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * this class is the validator for controller methods
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-26
 */

@Component
public class Validator {

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    /**
     * <p>this method will check if the card number value is correct and not exist for register
     * </p>
     *
     * @param cardNumber is amount of requested card number
     * @return this method will return boolean value after check the card number
     * @since 1.0
     */
    public boolean cardNumberExisting(Long cardNumber) {
        if(cardNumber > 999999999999999L && cardNumber < 10000000000000000L){
            Card cardObj = cardService.fetchCardByCardNumber(cardNumber);
            return cardObj == null;
        }
        return false;
    }

    /**
     * <p>this method will check if the card number value is correct and exist for login
     * </p>
     *
     * @param cardNumber is amount of requested card number
     * @return this method will return boolean value after check the card number
     * @since 1.0
     */
    public boolean cardNumberValidation(Long cardNumber) {
        if (cardNumber > 999999999999999L && cardNumber < 10000000000000000L) {
            Card cardObj = cardService.fetchCardByCardNumber(cardNumber);
            return cardObj != null;
        }
        return true;
    }

    /**
     * <p>this method will check if the password value is correct
     * </p>
     *
     * @param pass is amount of requested password
     * @return this method will return boolean value after check the password
     * @since 1.0
     */
    public boolean passwordValidation(String pass) {
        int password = Integer.parseInt(pass);
        return password > 999 && password < 10000;
    }

    /**
     * <p>after checking card number and password this method will check card can login or not
     * </p>
     *
     * @param pass is amount of requested password
     * @param originalCardNumber is amount of requested card number
     * @return this method will return boolean value after check the login
     * @since 1.0
     */
    public boolean checkLogin(String pass, Long originalCardNumber) throws NoSuchAlgorithmException {
        String hashedPass = Configuration.passwordHash(pass);
        Card card = cardService.fetchCardByCardNumberAndPassword(originalCardNumber, hashedPass);
        if (card != null) {
            int wrongCount = card.getWrongCount();
            return wrongCount < 3;
        }
        return false;
    }

    /**
     * <p>this method will check the balance amount
     * </p>
     *
     * @param amount is amount of requested amount
     * @return this method will return boolean value after check the amount
     * @since 1.0
     */
    public boolean balanceValidation(Long amount) {
        return amount > 0;
    }

    /**
     * <p>this method will check the destination card number is exist or not
     * </p>
     *
     * @param destinationCardNumber is amount of requested destinationCardNumber
     * @return this method will return boolean value after check the destinationCardNumber
     * @since 1.0
     */
    public boolean checkDestinationCardNumber(Long destinationCardNumber) {
        Card cardObj = cardService.fetchCardByCardNumber(destinationCardNumber);
        return cardObj != null;
    }

    /**
     * <p>this method will check the date is validate or not
     * </p>
     *
     * @param transactionDate is string amount of date
     * @return this method will return boolean value after check the transaction date
     * @since 1.0
     */
    public boolean dateValidator(String transactionDate) {

        String pattern = "yyyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String todayAsString = dateFormat.format(today);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = format.parse(transactionDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        Date date1 = calendar.getTime();
        String todayFromClient = dateFormat.format(date1);

        return todayFromClient.equals(todayAsString);
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

    /**
     * <p>this method will validate for login the card and if the card validate, save it to database
     * </p>
     *
     * @param transactionObj is an object to save transaction during transaction start to end.
     * @param password is string amount of password.
     * @return this method will return a card object if it valid or not
     * @since 1.0
     */
    public boolean cardValidationAndSave(Transaction transactionObj, String password)
            throws NoSuchAlgorithmException, NumberFormatException {
        Card cardObj = cardService.fetchCardByCardNumber(transactionObj.getOriginalCardNumber());
        transactionObj.setCard(new Card(cardObj.getId()));
        boolean passwordValidation = passwordValidation(password);
        boolean checkLogin = checkLogin(password, transactionObj.getOriginalCardNumber());

        if (passwordValidation && checkLogin) {

            return true;
        } else {

            return false;
        }
    }

    /**
     * <p>this method will validate for repetition of transaction
     * </p>
     *
     * @param originalCardNumber is the number of card.
     * @param transactionDate is the date of transaction.
     * @param trackingNumber is the tracking number.
     * @param terminalType is the type of terminal that send the request.
     * @return this method will return true or false of repetition transaction.
     * @since 1.0
     */
    public boolean repetitionTransactionValidator(Long originalCardNumber, String transactionDate,
            Long trackingNumber, String terminalType, String responseCode) {

        Transaction transaction =
                transactionService.fetchTransactionByOriginalCardNumberAndTransactionDateAndTrackingNumberAndTerminalTypeAndResponseCode(
                originalCardNumber, transactionDate, trackingNumber, terminalType, responseCode);
        return transaction == null;
    }

}
