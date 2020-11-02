package ir.dotin.card_transactions.validation;

import ir.dotin.card_transactions.entity.Transaction;
import ir.dotin.card_transactions.service.TransactionService;
import org.springframework.stereotype.Service;

/**
 * this class is the validator for repetition of transaction
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-11-02
 */
@Service
public class TransactionRepetitionValidation {
    private final TransactionService transactionService;

    public TransactionRepetitionValidation(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * <p>this method will validate for repetition of transaction
     * </p>
     *
     * @param originalCardNumber is the number of card.
     * @param transactionDate    is the date of transaction.
     * @param trackingNumber     is the tracking number.
     * @param terminalType       is the type of terminal that send the request.
     * @param responseCode       is the code of response that answered by server.
     * @return this method will return true or false of repetition transaction.
     * @since 1.0
     */
    public boolean repetitionTransactionValidator(Long originalCardNumber, String transactionDate,
                                                  Long trackingNumber, String terminalType, String responseCode) {
        Transaction transaction =
                transactionService.fetchTransactionByOriginalCardNumberAndTransactionDateAndTrackingNumberAndTerminalTypeAndResponseCode(
                        originalCardNumber, transactionDate, trackingNumber, terminalType, responseCode);
        return transaction == null;
    }
}
