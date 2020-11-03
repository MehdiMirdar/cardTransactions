package ir.dotin.card_transactions.service.validation;

import ir.dotin.card_transactions.entity.Card;
import org.springframework.stereotype.Service;

/**
 * this class is the validator for balance amount
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-11-02
 */
@Service
public class BalanceValidation implements ICardValidation {

    /**
     * <p>this method will check the balance amount
     * </p>
     *
     * @param card is amount of card that contains requested amount
     * @return this method will return boolean value after check the amount
     * @since 1.0
     */
    @Override
    public boolean validate(Card card) {
        return card.getBalance() > 0;
    }
}
