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

    public Card() {
    }

    public Card(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private Long cardNumber;

    private Long balance;

    private String password;

    @Column(name = "password_condition")
    private int passwordCondition;

    @Column(name = "wrong_count")
    private int wrongCount;

    private String message;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
    private Set<Transaction> transactions;
}
