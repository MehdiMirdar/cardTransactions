package ir.dotin.card_transactions.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

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
    private String transactionDate;

    @Column(name = "terminal_type")
    private String terminalType;

    @Column(name = "tracking_number")
    private Long trackingNumber;

    @Column(name = "original_card_number")
    private Long originalCardNumber;

    private Long amount;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "transaction_type")
    private int transactionType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

}
