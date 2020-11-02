package ir.dotin.card_transactions.validation;

import ir.dotin.card_transactions.entity.Card;
import org.springframework.stereotype.Service;

/**
 * this class is the validator for password amount
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-11-02
 */
@Service
public class PasswordValidation implements ICardValidation {

    /**
     * <p>this method will check if the password value is correct
     * </p>
     *
     * @param card is amount of card that contains requested password
     * @return this method will return boolean value after check the password
     * @since 1.0
     */
    @Override
    public boolean validate(Card card) {
        int password = Integer.parseInt(card.getPassword());
        return password > 999 && password < 10000;
    }
}
