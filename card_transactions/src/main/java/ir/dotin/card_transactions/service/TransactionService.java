package ir.dotin.card_transactions.service;

import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction fetchTransactionByCardNumberAndTrackingNumber(
            Long trackingNumber, Long originalCardNumber) {
        return transactionRepository.findByTrackingNumberAndOriginalCardNumber(trackingNumber, originalCardNumber);
    }

    public Transaction fetchTransactionByTrackingNumber(Long trackingNumber) {
        return transactionRepository.findByTrackingNumber(trackingNumber);
    }

    public List<Transaction> fetchLastTen10ByOriginalCardNumber(Long originalCardNumber) {
        return transactionRepository.findFirst10ByOriginalCardNumberOrderByIdDesc(originalCardNumber);
    }

    public List<Transaction> fetchAllByOriginalCardNumberAndTransactionDate(Long originalCardNumber,
                                                                            String startDate, String endDate) {
        return transactionRepository.findDistinctByTransactionDateBetweenAndOriginalCardNumber(originalCardNumber,
                startDate, endDate);
    }
}
