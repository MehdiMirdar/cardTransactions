package ir.dotin.card_transactions.validation;

import ir.dotin.card_transactions.entity.Card;

public interface ICardValidation {

    boolean validate(Card card);
}
