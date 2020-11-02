package ir.dotin.card_transactions.validation;

import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.service.CardService;
import org.springframework.stereotype.Service;

/**
 * this class is the validator for destination card number
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-11-02
 */
@Service
public class DestinationCardNumberValidation {
    private final CardService cardService;

    public DestinationCardNumberValidation(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * <p>this method will check the destination card number is exist or not
     * </p>
     *
     * @param destinationCardNumber is amount of requested destinationCardNumber
     * @return this method will return boolean value after check the destinationCardNumber
     * @since 1.0
     */
    public boolean destinationCardNumberValidator(Long destinationCardNumber) {
        Card cardObj = cardService.fetchCardByCardNumber(destinationCardNumber);
        return cardObj != null;
    }
}
