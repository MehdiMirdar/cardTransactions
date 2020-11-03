package ir.dotin.card_transactions.controller;

import ir.dotin.card_transactions.config.Configuration;
import ir.dotin.card_transactions.service.converter.CardConverter;
import ir.dotin.card_transactions.dto.CardDto;
import ir.dotin.card_transactions.service.converter.TransactionConverter;
import ir.dotin.card_transactions.dto.TransactionDto;
import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.service.CardService;
import ir.dotin.card_transactions.service.TransactionService;
import ir.dotin.card_transactions.service.validation.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * this class is the Controller for card transaction
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-27
 */
@RestController
public class CardTransactionController {

    private final LoginValidation loginValidation;
    private final CardService cardService;
    private final TransactionService transactionService;
    private final CardNumberExisting cardNumberExisting;
    private final PasswordValidation passwordValidation;
    private final BalanceValidation balanceValidation;
    private final Configuration configuration;
    private final TransactionRepetitionValidation transactionRepetitionValidation;
    private final DateValidation dateValidation;
    private final DestinationCardNumberValidation destinationCardNumberValidation;
    private final OriginalCardNumberValidation originalCardNumberValidation;
    private final CardConverter cardConverter;
    private final TransactionConverter transactionConverter;

    @Autowired
    public CardTransactionController(LoginValidation loginValidation, CardService cardService, TransactionService transactionService,
                                     CardNumberExisting cardNumberExisting, PasswordValidation passwordValidation,
                                     BalanceValidation balanceValidation, Configuration configuration,
                                     TransactionRepetitionValidation transactionRepetitionValidation,
                                     DateValidation dateValidation, DestinationCardNumberValidation destinationCardNumberValidation,
                                     OriginalCardNumberValidation originalCardNumberValidation,
                                     CardConverter cardConverter, TransactionConverter transactionConverter) {
        this.loginValidation = loginValidation;
        this.cardService = cardService;
        this.transactionService = transactionService;
        this.cardNumberExisting = cardNumberExisting;
        this.passwordValidation = passwordValidation;
        this.balanceValidation = balanceValidation;
        this.configuration = configuration;
        this.transactionRepetitionValidation = transactionRepetitionValidation;
        this.dateValidation = dateValidation;
        this.destinationCardNumberValidation = destinationCardNumberValidation;
        this.originalCardNumberValidation = originalCardNumberValidation;
        this.cardConverter = cardConverter;
        this.transactionConverter = transactionConverter;
    }

    /**
     * <p>this method will register the new card
     * </p>
     *
     * @param dto is amount of requested card
     * @return this method will return the card values after registration
     * @since 1.0
     */
    @PostMapping(path = "/addnewcard")
    public CardDto registerCard(@RequestBody CardDto dto) throws NoSuchAlgorithmException {
        Card card = cardConverter.dtoToEntity(dto);
        String password = card.getPassword();
        card.setPasswordCondition(1);
        List<ICardValidation> validators = new ArrayList<ICardValidation>();
        validators.add(cardNumberExisting);
        validators.add(passwordValidation);
        validators.add(balanceValidation);
        for (ICardValidation validator : validators) {
            if (!validator.validate(card)) {
                throw new RuntimeException("Validator " + validator.getClass().getSimpleName() + " returns false.");
            }
        }
        String pass = configuration.hashPassword(password);
        card.setPassword(pass);
        cardService.saveCard(card);
        card.setPassword(password);
        return cardConverter.entityToDto(card);
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
    public JSONObject cardBalance(@RequestBody String str) {
        JSONObject obj = new JSONObject();
        Card validCard = null;
        TransactionDto transactionObjDto = new TransactionDto();
        transactionObjDto.setResponseCode("00");

        JSONObject json = null;
        try {
            json = configuration.strToJson(str);
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
        transactionObjDto.setAmount(0L);
        transactionObjDto.setTransactionDate(transactionDate);
        transactionObjDto.setTerminalType(terminalType);
        transactionObjDto.setCardNumber(originalCardNumber);
        transactionObjDto.setTransactionType(0);
        transactionObjDto.setTrackingNumber(trackingNumber);

        Transaction transactionObj = transactionConverter.dtoToEntity(transactionObjDto);

        boolean cardNumberValidation = originalCardNumberValidation.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = loginValidation.cardValidator(transactionObj, password);
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
            hashedPass = configuration.hashPassword(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);
        boolean dateValidator = dateValidation.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
            return obj;
        }

        boolean repetitionTransactionValidator = transactionRepetitionValidation.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
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
    public JSONObject lastTenTransaction(@RequestBody String str) {
        JSONObject obj = new JSONObject();
        Card validCard = null;
        TransactionDto transactionObjDto = new TransactionDto();
        transactionObjDto.setResponseCode("00");
        List<Transaction> transactionList = null;

        JSONObject json = null;
        try {
            json = configuration.strToJson(str);
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

        transactionObjDto.setAmount(0L);
        transactionObjDto.setTransactionDate(transactionDate);
        transactionObjDto.setTerminalType(terminalType);
        transactionObjDto.setCardNumber(originalCardNumber);
        transactionObjDto.setTrackingNumber(trackingNumber);
        transactionObjDto.setTransactionType(1);

        Transaction transactionObj = transactionConverter.dtoToEntity(transactionObjDto);

        boolean cardNumberValidation = originalCardNumberValidation.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = loginValidation.cardValidator(transactionObj, password);
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
            hashedPass = configuration.hashPassword(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);
        boolean dateValidator = dateValidation.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
            return obj;
        }

        boolean repetitionTransactionValidator = transactionRepetitionValidation.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
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
    public JSONObject cardToCard(@RequestBody String str) {
        JSONObject obj = new JSONObject();
        Card validCard = null;
        Card cardDestination = null;
        TransactionDto transactionObjDto = new TransactionDto();
        transactionObjDto.setResponseCode("00");
        List<Transaction> transactionList = null;

        JSONObject json = null;
        try {
            json = configuration.strToJson(str);
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

        transactionObjDto.setAmount(0L);
        transactionObjDto.setTransactionDate(transactionDate);
        transactionObjDto.setTerminalType(terminalType);
        transactionObjDto.setCardNumber(originalCardNumber);
        transactionObjDto.setTransactionType(2);
        transactionObjDto.setTrackingNumber(trackingNumber);

        Transaction transactionObj = transactionConverter.dtoToEntity(transactionObjDto);

        boolean cardNumberValidation = originalCardNumberValidation.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = loginValidation.cardValidator(transactionObj, password);
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
            hashedPass = configuration.hashPassword(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);
        boolean dateValidator = dateValidation.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
            return obj;
        }

        boolean repetitionTransactionValidator = transactionRepetitionValidation.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
            return obj;
        }

        if (validCard.getBalance() > amount) {
            boolean checkDestinationCardNumber = destinationCardNumberValidation.destinationCardNumberValidator(destinationCardNumber);
            if (checkDestinationCardNumber) {
                transactionList = transactionService.fetchLastTen10ByOriginalCardNumber(originalCardNumber);
                validCard.setBalance(validCard.getBalance() - amount);
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
        validCard.setWrongCount(0);
        cardService.saveCard(validCard);
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
    public JSONObject dailyTransaction(@RequestBody String str) {
        JSONObject obj = new JSONObject();
        Card validCard = null;
        TransactionDto transactionObjDto = new TransactionDto();
        transactionObjDto.setResponseCode("00");
        List<Transaction> transactionList = null;

        JSONObject json = null;
        try {
            json = configuration.strToJson(str);
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

        transactionObjDto.setAmount(0L);
        transactionObjDto.setTransactionDate(transactionDate);
        transactionObjDto.setTerminalType(terminalType);
        transactionObjDto.setCardNumber(originalCardNumber);
        transactionObjDto.setTransactionType(3);
        transactionObjDto.setTrackingNumber(trackingNumber);

        Transaction transactionObj = transactionConverter.dtoToEntity(transactionObjDto);

        boolean cardNumberValidation = originalCardNumberValidation.cardNumberValidation(originalCardNumber);
        if (!cardNumberValidation) {
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("responseCode", "15");
            return obj;
        }

        String hashedPass = null;
        try {
            boolean cardValidation = loginValidation.cardValidator(transactionObj, password);
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
            hashedPass = configuration.hashPassword(password);
        } catch (NoSuchAlgorithmException | NumberFormatException e) {
            e.printStackTrace();
            obj.put("responseCode", "80");
            return obj;
        }

        validCard = cardService.fetchCardByCardNumberAndPassword(transactionObj.getOriginalCardNumber(), hashedPass);
        boolean dateValidator = dateValidation.dateValidator(transactionDate);
        if (!dateValidator) {
            transactionObj.setResponseCode("77");
            obj.put("cardNumber", originalCardNumber);
            obj.put("trackingNumber", trackingNumber);
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
            return obj;
        }

        boolean repetitionTransactionValidator = transactionRepetitionValidation.repetitionTransactionValidator(originalCardNumber,
                transactionDate, trackingNumber, terminalType, transactionObj.getResponseCode());
        if (!repetitionTransactionValidator) {
            transactionObj.setResponseCode("94");
            obj.put("cardNumber", transactionObj.getOriginalCardNumber());
            obj.put("trackingNumber", transactionObj.getTrackingNumber());
            obj.put("responseCode", transactionObj.getResponseCode());
            transactionObj.setCard(new Card(validCard.getId()));
            transactionService.saveTransaction(transactionObj);
            validCard.setWrongCount(0);
            cardService.saveCard(validCard);
            return obj;
        }

        if (validCard.getBalance() > 1000) {
            transactionList = transactionService.fetchAllByOriginalCardNumberAndTransactionDate(originalCardNumber,
                    startDate, endDate);
            if (transactionList != null) {
                validCard.setBalance(validCard.getBalance() - 1000);
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
        validCard.setWrongCount(0);
        cardService.saveCard(validCard);
        obj.put("cartNumber", transactionObj.getOriginalCardNumber());
        obj.put("trackingNumber", transactionObj.getTrackingNumber());
        obj.put("responseCode", transactionObj.getResponseCode());
        obj.put("transactions", transactionList);
        return obj;
    }
}
