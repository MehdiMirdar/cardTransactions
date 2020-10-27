package ir.dotin.card_transactions.controller;

import ir.dotin.card_transactions.config.Configuration;
import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.service.CardService;
import ir.dotin.card_transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

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
     * <p>this method will check and make the unique tracking number
     * </p>
     *
     * @return this method will return long value of tracking number
     * @since 1.0
     */
    public Long trackingNumberMaker() {
        Long trackingNumber = 0L;
        while (true) {
            long min = 999999999999L;
            long max = 10000000000000L;
            trackingNumber = min + (long) (Math.random() * (max - min));
            Transaction transactionObj = transactionService.fetchTransactionByTrackingNumber(trackingNumber);
            if (transactionObj == null) {
                break;
            }
        }
        return trackingNumber;
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
}
