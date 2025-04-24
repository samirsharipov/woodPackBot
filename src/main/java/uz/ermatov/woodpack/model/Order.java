package uz.ermatov.woodpack.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Set<Long> productIdList;

    private long userId;

    private long telegramId;

    private boolean confirm;

    private Boolean acceptance;
}
