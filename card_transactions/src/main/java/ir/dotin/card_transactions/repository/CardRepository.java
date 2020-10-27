package ir.dotin.card_transactions.repository;


import ir.dotin.card_transactions.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * this interface is jpaRepository for card
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-26
 */

public interface CardRepository extends JpaRepository<Card, Long> {

    Card findByCardNumber(Long cardNumber);

    Card findByCardNumberAndPassword(Long cardNumber, String password);

}
