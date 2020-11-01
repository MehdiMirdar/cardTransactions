package ir.dotin.card_transactions.controller;

import ir.dotin.card_transactions.config.Configuration;
import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.service.CardService;
import ir.dotin.card_transactions.service.TransactionService;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * this class is the Controller
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-27
 */

@RestController
public class Controller {

    @Autowired
    private Validator validator;

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    /**
     * <p>this method will register the new card
     * </p>
     *
     * @param card is amount of requested card
     * @return this method will return the card values after registration
     * @since 1.0
     */

    @PostMapping(path = "/addnewcard")
    public Card registerCard(@RequestBody Card card) throws NoSuchAlgorithmException {
        Long cardNumber = card.getCardNumber();
        String password = card.getPassword();
        card.setPasswordCondition(1);
        boolean cardNumberValidation = validator.cardNumberExisting(cardNumber);
        if (cardNumberValidation) {
            boolean passwordValidation = validator.passwordValidation(card.getPassword());
            if (passwordValidation) {
                boolean balanceValidation = validator.balanceValidation(card.getBalance());
                if (balanceValidation) {
                    String pass = Configuration.passwordHash(password);
                    card.setPassword(pass);
                    cardService.saveCard(card);
                    card.setPassword(password);
                    return card;
                }
                return card;
            }
            return card;
        }
        return card;
    }

    /**
     * <p>this method will response the request about card balance
     * </p>
     *
     * @param str is amount of requested amounts for card balance
     * @return this method will return the json object for response to client
     * @since 1.0
     */
    @PostMapping(path = "/cardbalance")
    public synchronized JSONObject cardBalance(@RequestBody String str) {

        JSONObject obj = new JSONObject();
        Card validCard = null;
        Transaction transactionObj = new Transaction();
        transactionObj.setResponseCode("00");

        JSONObject json = null;
        try {
            json = validator.strToJson(str);
        } catch (ParseException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        if (json.get("cardNumber") == null || json.get("password") == null
                || "".equals(json.get("password")) || json.get("transactionDate") == null ||
                "".equals(json.get("transactionDate")) || json.get("terminalType") == null ||
                "".equals(json.get("terminalType")) || json.get("trackingNumber") == null) {
            obj.put("responseCode", "12");
            return obj;
        }

        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
        Long trackingNumber = (Long) json.get("trackingNumber");

        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTransactionType(0);
        transactionObj.setTrackingNumber(trackingNumber);

        boolean cardNumberValidation = validator.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = validator.cardValidationAndSave(transactionObj, password);
            if (!cardValidation) {
                Card cardObj = cardService.fetchCardByCardNumber(transactionObj.getOriginalCardNumber());
                cardObj.setWrongCount(cardObj.getWrongCount() + 1);
                cardService.saveCard(cardObj);
                transactionObj.setResponseCode("57");
                transactionService.saveTransaction(transactionObj);
                obj.put("cardNumber", transactionObj.getOriginalCardNumber());
                obj.put("trackingNumber", transactionObj.getTrackingNumber());
                obj.put("responseCode", transactionObj.getResponseCode());
                return obj;
            }
            hashedPass = Configuration.passwordHash(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);

        boolean dateValidator = validator.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        boolean repetitionTransactionValidator = validator.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        validCard.setWrongCount(0);
        cardService.saveCard(validCard);
        transactionObj.setResponseCode("00");
        transactionService.saveTransaction(transactionObj);
        obj.put("balance", validCard.getBalance());
        obj.put("cartNumber", transactionObj.getOriginalCardNumber());
        obj.put("trackingNumber", transactionObj.getTrackingNumber());
        obj.put("responseCode", transactionObj.getResponseCode());

        return obj;
    }

    /**
     * <p>this method will response the request about last 10 transactions
     * </p>
     *
     * @param str is amount of requested amounts for last 10 transactions
     * @return this method will return the json object for response to client
     * @since 1.0
     */

    @PostMapping(path = "/last10transaction")
    public synchronized JSONObject lastTenTransaction(@RequestBody String str) {

        JSONObject obj = new JSONObject();
        Card validCard = null;
        Transaction transactionObj = new Transaction();
        transactionObj.setResponseCode("00");
        List<Transaction> transactionList = null;

        JSONObject json = null;
        try {
            json = validator.strToJson(str);
        } catch (ParseException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        if (json.get("cardNumber") == null || json.get("password") == null
                || "".equals(json.get("password")) || json.get("transactionDate") == null ||
                "".equals(json.get("transactionDate")) || json.get("terminalType") == null ||
                "".equals(json.get("terminalType")) || json.get("trackingNumber") == null) {
            obj.put("responseCode", "12");
            return obj;
        }

        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
        Long trackingNumber = (Long) json.get("trackingNumber");

        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTrackingNumber(trackingNumber);
        transactionObj.setTransactionType(1);

        boolean cardNumberValidation = validator.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = validator.cardValidationAndSave(transactionObj, password);
            if (!cardValidation) {
                Card cardObj = cardService.fetchCardByCardNumber(transactionObj.getOriginalCardNumber());
                cardObj.setWrongCount(cardObj.getWrongCount() + 1);
                cardService.saveCard(cardObj);
                transactionObj.setResponseCode("57");
                transactionService.saveTransaction(transactionObj);
                obj.put("cardNumber", transactionObj.getOriginalCardNumber());
                obj.put("trackingNumber", transactionObj.getTrackingNumber());
                obj.put("responseCode", transactionObj.getResponseCode());
                return obj;
            }
            hashedPass = Configuration.passwordHash(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);

        boolean dateValidator = validator.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        boolean repetitionTransactionValidator = validator.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        validCard.setWrongCount(0);
        cardService.saveCard(validCard);
        transactionList = transactionService.fetchLastTen10ByOriginalCardNumber(originalCardNumber);
        transactionObj.setCard(new Card(validCard.getId()));
        transactionObj.setResponseCode("00");
        transactionService.saveTransaction(transactionObj);
        obj.put("cartNumber", transactionObj.getOriginalCardNumber());
        obj.put("trackingNumber", transactionObj.getTrackingNumber());
        obj.put("responseCode", transactionObj.getResponseCode());
        obj.put("transactions", transactionList);

        return obj;

    }

    /**
     * <p>this method will response the request about card to card
     * </p>
     *
     * @param str is amount of requested amounts for card to card
     * @return this method will return the json object for response to client
     * @since 1.0
     */

    @PostMapping(path = "/cardtocard")
    public synchronized JSONObject cardToCard(@RequestBody String str) {

        JSONObject obj = new JSONObject();
        Card validCard = null;
        Card cardDestination = null;
        Transaction transactionObj = new Transaction();
        transactionObj.setResponseCode("00");
        List<Transaction> transactionList = null;

        JSONObject json = null;
        try {
            json = validator.strToJson(str);
        } catch (ParseException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        if (json.get("cardNumber") == null || json.get("destinationCardNumber") == null ||
                json.get("password") == null || "".equals(json.get("password")) ||
                json.get("transactionDate") == null || "".equals(json.get("transactionDate")) ||
                json.get("terminalType") == null || "".equals(json.get("terminalType")) ||
                json.get("trackingNumber") == null || json.get("amount") == null) {
            obj.put("responseCode", "12");
            return obj;
        }

        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
        Long trackingNumber = (Long) json.get("trackingNumber");
        Long destinationCardNumber = (Long) json.get("destinationCardNumber");
        Long amount = (Long) json.get("amount");

        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTransactionType(2);
        transactionObj.setTrackingNumber(trackingNumber);

        boolean cardNumberValidation = validator.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = validator.cardValidationAndSave(transactionObj, password);
            if (!cardValidation) {
                Card cardObj = cardService.fetchCardByCardNumber(transactionObj.getOriginalCardNumber());
                cardObj.setWrongCount(cardObj.getWrongCount() + 1);
                cardService.saveCard(cardObj);
                transactionObj.setResponseCode("57");
                transactionService.saveTransaction(transactionObj);
                obj.put("cardNumber", transactionObj.getOriginalCardNumber());
                obj.put("destinationCardNumber", destinationCardNumber);
                obj.put("trackingNumber", transactionObj.getTrackingNumber());
                obj.put("responseCode", transactionObj.getResponseCode());
                return obj;
            }
            hashedPass = Configuration.passwordHash(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);

        boolean dateValidator = validator.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        boolean repetitionTransactionValidator = validator.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        if (validCard.getBalance() > amount) {
            boolean checkDestinationCardNumber = validator.checkDestinationCardNumber(destinationCardNumber);
            if (checkDestinationCardNumber) {
                transactionList = transactionService.fetchLastTen10ByOriginalCardNumber(originalCardNumber);
                validCard.setBalance(validCard.getBalance() - amount);
                validCard.setWrongCount(0);
                cardService.saveCard(validCard);
                cardDestination = cardService.fetchCardByCardNumber(destinationCardNumber);
                cardDestination.setBalance(cardDestination.getBalance() + amount);
                cardService.saveCard(cardDestination);

                if (transactionList != null) {
                    transactionObj.setResponseCode("00");
                    transactionObj.setAmount(-amount);
                    transactionObj.setCard(new Card(validCard.getId()));
                    transactionService.saveTransaction(transactionObj);

                } else {
                    transactionList = new ArrayList<>();
                }

            } else {
                transactionObj.setResponseCode("16");
                transactionObj.setCard(new Card(validCard.getId()));
                transactionService.saveTransaction(transactionObj);
                transactionList = new ArrayList<>();
            }
        } else {
            transactionObj.setResponseCode("51");
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            transactionList = new ArrayList<>();
        }
        obj.put("cartNumber", transactionObj.getOriginalCardNumber());
        obj.put("destinationCardNumber", destinationCardNumber);
        obj.put("trackingNumber", transactionObj.getTrackingNumber());
        obj.put("responseCode", transactionObj.getResponseCode());
        obj.put("transactions", transactionList);

        return obj;
    }

    /**
     * <p>this method will response the request about daily transactions
     * </p>
     *
     * @param str is amount of requested amounts for daily transactions
     * @return this method will return the json object for response to client
     * @since 1.0
     */

    @PostMapping(path = "/dailytransaction")
    public synchronized JSONObject dailyTransaction(@RequestBody String str) {

        JSONObject obj = new JSONObject();
        Card validCard = null;
        Transaction transactionObj = new Transaction();
        transactionObj.setResponseCode("00");
        List<Transaction> transactionList = null;

        JSONObject json = null;
        try {
            json = validator.strToJson(str);
        } catch (ParseException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        if (json.get("cardNumber") == null || json.get("password") == null
                || "".equals(json.get("password")) || json.get("transactionDate") == null ||
                "".equals(json.get("transactionDate")) || json.get("terminalType") == null ||
                "".equals(json.get("terminalType")) || json.get("trackingNumber") == null ||
                json.get("startDate") == null || "".equals(json.get("startDate")) ||
                json.get("endDate") == null || "".equals(json.get("endDate"))) {
            obj.put("responseCode", "12");
            return obj;
        }

        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
        Long trackingNumber = (Long) json.get("trackingNumber");
        String startDate = (String) json.get("startDate");
        String endDate = (String) json.get("endDate");

        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTransactionType(3);
        transactionObj.setTrackingNumber(trackingNumber);

        boolean cardNumberValidation = validator.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = validator.cardValidationAndSave(transactionObj, password);
            if (!cardValidation) {
                Card cardObj = cardService.fetchCardByCardNumber(transactionObj.getOriginalCardNumber());
                cardObj.setWrongCount(cardObj.getWrongCount() + 1);
                cardService.saveCard(cardObj);
                transactionObj.setResponseCode("57");
                transactionService.saveTransaction(transactionObj);
                obj.put("cardNumber", transactionObj.getOriginalCardNumber());
                obj.put("trackingNumber", transactionObj.getTrackingNumber());
                obj.put("responseCode", transactionObj.getResponseCode());
                return obj;
            }
            hashedPass = Configuration.passwordHash(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);

        boolean dateValidator = validator.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        boolean repetitionTransactionValidator = validator.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            return obj;
        }

        if (validCard.getBalance() > 1000) {
            transactionList = transactionService.fetchAllByOriginalCardNumberAndTransactionDate(originalCardNumber,
                    startDate, endDate);
            if (transactionList != null) {
                validCard.setWrongCount(0);
                validCard.setBalance(validCard.getBalance() - 1000);
                cardService.saveCard(validCard);
                transactionObj.setAmount(-1000L);
                transactionObj.setResponseCode("00");
                transactionObj.setCard(new Card(validCard.getId()));
                transactionService.saveTransaction(transactionObj);
            } else {
                transactionList = new ArrayList<>();
            }
        } else {
            transactionObj.setResponseCode("51");
        }
        obj.put("cartNumber", transactionObj.getOriginalCardNumber());
        obj.put("trackingNumber", transactionObj.getTrackingNumber());
        obj.put("responseCode", transactionObj.getResponseCode());
        obj.put("transactions", transactionList);

        return obj;
    }

}
