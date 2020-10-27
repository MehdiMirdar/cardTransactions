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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
public class Controller {

    @Autowired
    private Validator validator;

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping(path = "/addnewcard")
    public Card registerCard(@RequestBody Card card) throws NoSuchAlgorithmException {
        Long cardNumber = card.getCardNumber();
        String password = card.getPassword();
        card.setPasswordCondition(1);
        boolean cardNumberValidation = validator.cardNumberValidation(cardNumber);
        if (cardNumberValidation) {
            boolean passwordValidation = validator.passwordValidation(card.getPassword());
            if (passwordValidation) {
                boolean balanceValidation = validator.balanceValidation(card.getBalance());
                if (balanceValidation) {
                    String pass = Configuration.passwordHash(password);
                    card.setPassword(pass);
                    card.setMessage("done");
                    cardService.saveCard(card);
                    card.setPassword(password);
                    return card;
                }
                card.setMessage("wrong value for balance");
                return card;
            }
            card.setMessage("wrong value for password");
            return card;
        }
        card.setMessage("wrong value for card number or the same value is saved in database");
        return card;
    }

    @PostMapping(path = "/cardbalance")
    public Transaction authCard(@RequestBody String str) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(str);
        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
//        Long trackingNumber = (Long) json.get("trackingNumber");

        Card cardObj = null;
        Card validCard = null;
        Transaction transactionObj = new Transaction();
        Long trackingNum = validator.trackingNumberMaker();
        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setTrackingNumber(trackingNum);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTransactionType(0);

        boolean cardNumberValidation = validator.cardNumberExisting(originalCardNumber);

        if (cardNumberValidation) {
            cardObj = cardService.fetchCardByCardNumber(originalCardNumber);
            transactionObj.setCard(new Card(cardObj.getId()));
            boolean passwordValidation = validator.passwordValidation(password);
            boolean checkLogin = validator.checkLogin(password, originalCardNumber);
            if (passwordValidation && checkLogin) {
                String hashedPass = Configuration.passwordHash(password);
                validCard = cardService.fetchCardByCardNumberAndPassword(originalCardNumber, hashedPass);
                validCard.setWrongCount(0);
                validCard.setMessage("done");
                cardService.saveCard(validCard);
            } else {
                cardObj.setMessage("invalid password");
                cardObj.setWrongCount(cardObj.getWrongCount() + 1);
                cardService.saveCard(cardObj);
                transactionObj.setResponseCode("57");
                transactionService.saveTransaction(transactionObj);
            }
        } else {
            transactionObj.setResponseCode("15");
        }

        if (validCard != null) {

            transactionObj.setCard(new Card(validCard.getId()));
            transactionObj.setResponseCode("00");
            transactionService.saveTransaction(transactionObj);
            transactionObj.setAmount(validCard.getBalance());
            return transactionObj;
        }
        return transactionObj;
    }


}
