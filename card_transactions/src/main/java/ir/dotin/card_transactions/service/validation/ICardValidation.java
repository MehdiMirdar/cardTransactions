package ir.dotin.card_transactions.service.validation;

import ir.dotin.card_transactions.entity.Card;

public interface ICardValidation {

    boolean validate(Card card);
}
