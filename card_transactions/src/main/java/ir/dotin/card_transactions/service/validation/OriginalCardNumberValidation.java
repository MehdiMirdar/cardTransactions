package ir.dotin.card_transactions.service.validation;

import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.service.CardService;
import org.springframework.stereotype.Service;

/**
 * this class is the validator for original card number
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-11-02
 */
@Service
public class OriginalCardNumberValidation {
    private final CardService cardService;

    public OriginalCardNumberValidation(CardService cardService) {
        this.cardService = cardService;
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
        return false;
    }
}
