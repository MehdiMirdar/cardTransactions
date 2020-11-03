package ir.dotin.card_transactions.service.validation;

import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * this class is the validator for card number
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-11-02
 */
@Service
public class CardNumberExisting implements ICardValidation {
    private final CardService cardService;

    @Autowired
    public CardNumberExisting(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * <p>this method will check if the card number value is correct and not exist for register
     * </p>
     *
     * @param card is amount of card that contains requested card number
     * @return this method will return boolean value after check the card number
     * @since 1.0
     */
    @Override
    public boolean validate(Card card) {
        if (card.getCardNumber() > 999999999999999L && card.getCardNumber() < 10000000000000000L) {
            Card cardObj = cardService.fetchCardByCardNumber(card.getCardNumber());
            return cardObj == null;
        }
        return false;
    }
}
