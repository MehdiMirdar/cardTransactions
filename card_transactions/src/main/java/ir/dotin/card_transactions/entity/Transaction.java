package ir.dotin.card_transactions.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_transaction")
@Getter
@Setter
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "terminal_type")
    private int terminalType;

    @Column(name = "tracking_number")
    private Long trackingNumber;

    @Column(name = "original_card_number")
    private Long originalCardNumber;

    private Long amount;

    @Column(name = "response_code")
    private int responseCode;

    @Column(name = "transaction_type")
    private int transactionType;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;
}
