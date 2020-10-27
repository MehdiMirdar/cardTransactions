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
import java.util.ArrayList;
import java.util.List;

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
    public Transaction authCard(@RequestBody String str) {

        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
        Long trackingNumber = (Long) json.get("trackingNumber");

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
            boolean checkLogin = false;
            try {
                checkLogin = validator.checkLogin(password, originalCardNumber);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if (passwordValidation && checkLogin) {
                String hashedPass = null;
                try {
                    hashedPass = Configuration.passwordHash(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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

    @PostMapping(path = "/last10transaction")
    public List<Transaction> lastTenTransaction(@RequestBody String str) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
//        Long trackingNumber = (Long) json.get("trackingNumber");

        Card cardObj = null;
        Card validCard = null;
        Transaction transactionObj = new Transaction();
        List<Transaction> transactionList = null;
        Long trackingNum = validator.trackingNumberMaker();
        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setTrackingNumber(trackingNum);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTransactionType(1);

        boolean cardNumberValidation = validator.cardNumberExisting(originalCardNumber);

        if (cardNumberValidation) {
            cardObj = cardService.fetchCardByCardNumber(originalCardNumber);
            transactionObj.setCard(new Card(cardObj.getId()));
            boolean passwordValidation = validator.passwordValidation(password);
            boolean checkLogin = false;
            try {
                checkLogin = validator.checkLogin(password, originalCardNumber);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if (passwordValidation && checkLogin) {
                String hashedPass = null;
                try {
                    hashedPass = Configuration.passwordHash(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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
            transactionList = transactionService.fetchLastTen10ByOriginalCardNumber(originalCardNumber);
            if (transactionList != null) {
                transactionObj.setCard(new Card(validCard.getId()));
                transactionObj.setResponseCode("00");
                transactionService.saveTransaction(transactionObj);
                transactionObj.setId(0L);
                transactionList.add(transactionObj);
                return transactionList;
            }
        }

        transactionObj.setId(0L);
        List<Transaction> invalidTransaction = new ArrayList<>();
        invalidTransaction.add(transactionObj);
        return invalidTransaction;
    }

    @PostMapping(path = "/cardtocard")
    public List<Transaction> cardToCard(@RequestBody String str) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
//        Long trackingNumber = (Long) json.get("trackingNumber");
        Long destinationCardNumber = (Long) json.get("destinationCardNumber");
        Long amount = (Long) json.get("amount");

        Card cardObj = null;
        Card validCard = null;
        Card cardDestination = null;
        Transaction transactionObj = new Transaction();
        Transaction transactionDest = new Transaction();
        List<Transaction> transactionList = null;
        Long trackingNum = validator.trackingNumberMaker();
        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setTrackingNumber(trackingNum);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTransactionType(2);

        boolean cardNumberValidation = validator.cardNumberExisting(originalCardNumber);

        if (cardNumberValidation) {
            cardObj = cardService.fetchCardByCardNumber(originalCardNumber);
            transactionObj.setCard(new Card(cardObj.getId()));
            boolean passwordValidation = validator.passwordValidation(password);
            boolean checkLogin = false;
            try {
                checkLogin = validator.checkLogin(password, originalCardNumber);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if (passwordValidation && checkLogin) {
                String hashedPass = null;
                try {
                    hashedPass = Configuration.passwordHash(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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
            if (validCard.getBalance() > amount) {
                boolean checkDestinationCardNumber = validator.checkDestinationCardNumber(destinationCardNumber);
                if (checkDestinationCardNumber) {
                    transactionList = transactionService.fetchLastTen10ByOriginalCardNumber(originalCardNumber);
                    validCard.setBalance(validCard.getBalance() - amount);
                    cardService.saveCard(validCard);
                    cardDestination = cardService.fetchCardByCardNumber(destinationCardNumber);
                    cardDestination.setBalance(cardDestination.getBalance() + amount);
                    cardService.saveCard(cardDestination);

                    if (transactionList != null) {
                        transactionObj.setResponseCode("00");
                        transactionObj.setAmount(-amount);
                        transactionObj.setCard(new Card(validCard.getId()));
                        transactionService.saveTransaction(transactionObj);
                        transactionObj.setId(0L);
                        transactionDest.setOriginalCardNumber(destinationCardNumber);
                        transactionDest.setId(-1L);

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

        } else {
            transactionObj.setResponseCode("15");
            transactionList = new ArrayList<>();
        }
        transactionList.add(transactionObj);
        transactionList.add(transactionDest);
        return transactionList;
    }

    @PostMapping(path = "/dailytransaction")
    public List<Transaction> dailyTransaction(@RequestBody String str) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long originalCardNumber = (Long) json.get("cardNumber");
        String password = String.valueOf(json.get("password"));
        String transactionDate = (String) json.get("transactionDate");
        String terminalType = (String) json.get("terminalType");
        Long trackingNumber = (Long) json.get("trackingNumber");
        String startDate = (String) json.get("startDate");
        String endDate = (String) json.get("endDate");

        Card cardObj = null;
        Card validCard = null;
        Transaction transactionObj = new Transaction();
        List<Transaction> transactionList = null;
        Long trackingNum = validator.trackingNumberMaker();
        transactionObj.setAmount(0L);
        transactionObj.setTransactionDate(transactionDate);
        transactionObj.setTerminalType(terminalType);
        transactionObj.setTrackingNumber(trackingNum);
        transactionObj.setOriginalCardNumber(originalCardNumber);
        transactionObj.setTransactionType(3);

        boolean cardNumberValidation = validator.cardNumberExisting(originalCardNumber);

        if (cardNumberValidation) {
            cardObj = cardService.fetchCardByCardNumber(originalCardNumber);
            transactionObj.setCard(new Card(cardObj.getId()));
            boolean passwordValidation = validator.passwordValidation(password);
            boolean checkLogin = false;
            try {
                checkLogin = validator.checkLogin(password, originalCardNumber);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if (passwordValidation && checkLogin) {
                String hashedPass = null;
                try {
                    hashedPass = Configuration.passwordHash(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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
            transactionList = transactionService.fetchAllByOriginalCardNumberAndTransactionDate(originalCardNumber,
                    startDate, endDate);
            if (transactionList != null) {
                validCard.setBalance(validCard.getBalance() - 1000);
                cardService.saveCard(validCard);

                transactionObj.setAmount(-1000L);
                transactionObj.setResponseCode("00");
                transactionObj.setCard(new Card(cardObj.getId()));
                transactionService.saveTransaction(transactionObj);
                transactionObj.setId(0L);
            } else {
                transactionList = new ArrayList<>();
            }
        }else {
            transactionObj.setResponseCode("15");
            transactionList = new ArrayList<>();
        }
        transactionList.add(transactionObj);
        return transactionList;
    }

}
