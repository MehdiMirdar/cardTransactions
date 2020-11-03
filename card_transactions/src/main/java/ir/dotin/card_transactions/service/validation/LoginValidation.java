package ir.dotin.card_transactions.service.validation;

import ir.dotin.card_transactions.config.Configuration;
import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

/**
 * this class is the validator for login
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-26
 */
@Service
public class LoginValidation {
    private final CardService cardService;
    private final Configuration configuration;
    private final PasswordValidation passwordValidation;

    @Autowired
    public LoginValidation(CardService cardService, Configuration configuration, PasswordValidation passwordValidation) {
        this.cardService = cardService;
        this.configuration = configuration;
        this.passwordValidation = passwordValidation;
    }

    /**
     * <p>after checking card number and password this method will check card can login or not
     * </p>
     *
     * @param pass               is amount of requested password
     * @param originalCardNumber is amount of requested card number
     * @return this method will return boolean value after check the login
     * @since 1.0
     */
    public boolean checkLogin(String pass, Long originalCardNumber) throws NoSuchAlgorithmException {
        String hashedPass = configuration.hashPassword(pass);
        Card card = cardService.fetchCardByCardNumberAndPassword(originalCardNumber, hashedPass);
        if (card != null) {
            int wrongCount = card.getWrongCount();
            return wrongCount < 3;
        }
        return false;
    }

    /**
     * <p>this method will validate for login the card and if the card validate, save it to database
     * </p>
     *
     * @param transactionObj is an object to save transaction during transaction start to end.
     * @param password       is string amount of password.
     * @return this method will return a card object if it valid or not
     * @since 1.0
     */
    public boolean cardValidator(Transaction transactionObj, String password)
            throws NoSuchAlgorithmException, NumberFormatException {
        Card cardObj = cardService.fetchCardByCardNumber(transactionObj.getOriginalCardNumber());
        transactionObj.setCard(new Card(cardObj.getId()));
        Card card = new Card();
        card.setPassword(password);
        boolean passwordValidate = passwordValidation.validate(card);
        boolean checkLogin = checkLogin(password, transactionObj.getOriginalCardNumber());
        return passwordValidate && checkLogin;
    }
}
