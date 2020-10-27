package ir.dotin.card_transactions.repository;


import ir.dotin.card_transactions.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Card findByCardNumber(Long cardNumber);

    Card findByCardNumberAndPassword(Long cardNumber, String password);

}
