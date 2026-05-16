package ru.yandex.marketapp.order.infrastructure.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "shop_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderJpaEntity {

    @Id
    private Long id;

    @Column("total_sum")
    private long totalSum;
}
