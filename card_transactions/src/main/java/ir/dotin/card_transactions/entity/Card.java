package ir.dotin.card_transactions.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

/**
 * this class is the model for cards
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-10-24
 */
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
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH}, mappedBy = "card")
    private Set<Transaction> transactions;
}
