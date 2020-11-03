package ir.dotin.card_transactions.dto;

import ir.dotin.card_transactions.entity.Card;
import lombok.Data;

@Data
public class TransactionDto {
    private Long cardNumber;
    private String transactionDate;
    private String terminalType;
    private Long trackingNumber;
    private Long amount;
    private int transactionType;
    private String responseCode;
}
