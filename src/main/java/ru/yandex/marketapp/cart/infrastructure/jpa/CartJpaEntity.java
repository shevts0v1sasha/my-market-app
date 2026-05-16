package ru.yandex.marketapp.cart.infrastructure.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
public class CartJpaEntity {

    @Id
    private Long id;

    public CartJpaEntity(Long id) {
        this.id = id;
    }
}
