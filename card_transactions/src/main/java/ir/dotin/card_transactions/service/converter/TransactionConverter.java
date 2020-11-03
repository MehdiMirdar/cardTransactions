package ir.dotin.card_transactions.service.converter;

import ir.dotin.card_transactions.dto.TransactionDto;
import ir.dotin.card_transactions.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionConverter {
    public TransactionDto entityToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setCardNumber(transaction.getOriginalCardNumber());
        dto.setResponseCode(transaction.getResponseCode());
        dto.setTerminalType(transaction.getTerminalType());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setTrackingNumber(transaction.getTrackingNumber());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        return dto;
    }

    public List<TransactionDto> entityToDto(List<Transaction> transactions) {
        return transactions.stream().map(x -> entityToDto(x)).collect(Collectors.toList());
    }

    public Transaction dtoToEntity(TransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setOriginalCardNumber(dto.getCardNumber());
        transaction.setResponseCode(dto.getResponseCode());
        transaction.setTerminalType(dto.getTerminalType());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setTrackingNumber(dto.getTrackingNumber());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setAmount(dto.getAmount());
        return transaction;
    }

    public List<Transaction> dtoToEntity(List<TransactionDto> dtos) {
        return dtos.stream().map(x -> dtoToEntity(x)).collect(Collectors.toList());
    }
}
