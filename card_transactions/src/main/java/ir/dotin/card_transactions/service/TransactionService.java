package ir.dotin.card_transactions.service;

import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * this class is the service class for transactions
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-26
 */
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> fetchLastTen10ByOriginalCardNumber(Long originalCardNumber) {
        return transactionRepository.findFirst10ByOriginalCardNumberOrderByIdDesc(originalCardNumber);
    }

    public List<Transaction> fetchAllByOriginalCardNumberAndTransactionDate(Long originalCardNumber,
                                                                            String startDate, String endDate) {
        return transactionRepository.findDistinctByTransactionDateBetweenAndOriginalCardNumber(originalCardNumber,
                startDate, endDate);
    }

    public Transaction fetchTransactionByOriginalCardNumberAndTransactionDateAndTrackingNumberAndTerminalTypeAndResponseCode(
            Long originalCardNumber, String transactionDate, Long trackingNumber, String terminalType, String responseCode) {
        return transactionRepository.findByOriginalCardNumberAndTransactionDateAndTrackingNumberAndTerminalTypeAndResponseCode(
                originalCardNumber, transactionDate, trackingNumber, terminalType, responseCode);
    }
}
