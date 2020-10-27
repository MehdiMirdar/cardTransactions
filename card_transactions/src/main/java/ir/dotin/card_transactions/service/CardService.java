package ir.dotin.card_transactions.service;

import ir.dotin.card_transactions.entity.Card;
import ir.dotin.card_transactions.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Card saveCard(Card card) {
        return cardRepository.save(card);
    }

    public Card fetchCardByCardNumber(Long cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    public Card fetchCardByCardNumberAndPassword(Long cardNumber, String password) {
        return cardRepository.findByCardNumberAndPassword(cardNumber, password);
    }
}
