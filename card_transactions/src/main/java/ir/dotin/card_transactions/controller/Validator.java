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

    public boolean cardNumberValidation(Long cardNumber) {
        if(cardNumber > 999999999999999L && cardNumber < 10000000000000000L){
            Card cardObj = cardService.fetchCardByCardNumber(cardNumber);
            return cardObj == null;
        }
        return false;
    }

    public boolean cardNumberExisting(Long cardNumber) {
        if (cardNumber > 999999999999999L && cardNumber < 10000000000000000L) {
            Card cardObj = cardService.fetchCardByCardNumber(cardNumber);
            return cardObj != null;
        }
        return true;
    }

    public boolean passwordValidation(String pass) {
        int password = Integer.parseInt(pass);
        return password > 999 && password < 10000;
    }

    public boolean checkLogin(String pass, Long originalCardNumber) throws NoSuchAlgorithmException {
        String hashedPass = Configuration.passwordHash(pass);
        Card card = cardService.fetchCardByCardNumberAndPassword(originalCardNumber, hashedPass);
        if (card != null) {
            int wrongCount = card.getWrongCount();
            return wrongCount < 3;
        }
        return false;
    }

    public boolean balanceValidation(Long amount) {
        return amount > 0;
    }

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

    public boolean checkDestinationCardNumber(Long destinationCardNumber) {
        Card cardObj = cardService.fetchCardByCardNumber(destinationCardNumber);
        return cardObj != null;
    }
}
