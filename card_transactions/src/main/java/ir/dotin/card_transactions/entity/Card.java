package ir.dotin.card_transactions.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_card")
@Getter
@Setter
@ToString
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private Long cardNumber;

    private Long balance;

    private String password;

    @Column(name = "password_condition")
    private boolean passwordCondition;

    @Column(name = "wrong_counter")
    private int wrongCounter;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
    private Set<Transaction> transactions;
}
