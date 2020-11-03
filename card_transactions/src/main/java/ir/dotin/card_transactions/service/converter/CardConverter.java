package ir.dotin.card_transactions.service.converter;

import ir.dotin.card_transactions.dto.CardDto;
import ir.dotin.card_transactions.entity.Card;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardConverter {
    public CardDto entityToDto(Card card) {
        CardDto dto = new CardDto();
        dto.setCardNumber(card.getCardNumber());
        dto.setBalance(card.getBalance());
        dto.setPassword(card.getPassword());
        return dto;
    }

    public List<CardDto> entityToDto(List<Card> cards) {
        return cards.stream().map(x -> entityToDto(x)).collect(Collectors.toList());
    }

    public Card dtoToEntity(CardDto dto) {
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setBalance(dto.getBalance());
        card.setPassword(dto.getPassword());
        return card;
    }

    public List<Card> dtoToEntity(List<CardDto> dtos) {
        return dtos.stream().map(x -> dtoToEntity(x)).collect(Collectors.toList());
    }

}
