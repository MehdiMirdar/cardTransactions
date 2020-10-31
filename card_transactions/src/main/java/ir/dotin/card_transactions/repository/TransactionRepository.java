package ir.dotin.card_transactions.repository;

import ir.dotin.card_transactions.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * this interface is jpaRepository for transactions
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-26
 */

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction findByTrackingNumberAndOriginalCardNumber(
            Long trackingNumber, Long originalCardNumber);

    Transaction findByTrackingNumber(Long trackingNumber);

    List<Transaction> findFirst10ByOriginalCardNumberOrderByIdDesc(Long cardNumber);

    @Query("select t from Transaction t where t.originalCardNumber = :originalCardNumber and t.transactionDate >=  :startDate and t.transactionDate <=  :endDate")
    List<Transaction> findDistinctByTransactionDateBetweenAndOriginalCardNumber(
            Long originalCardNumber, String startDate, String endDate);

    Transaction findByOriginalCardNumberAndTransactionDateAndTrackingNumberAndTerminalTypeAndResponseCode(Long originalCardNumber,
            String transactionDate, Long trackingNumber, String terminalType, String responseCode);

}
