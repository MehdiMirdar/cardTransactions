package ir.dotin.card_transactions.dto;

import lombok.Data;

@Data
public class CardDto {
    private Long cardNumber;
    private Long balance;
    private String password;
}
