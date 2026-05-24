package ru.yandex.marketapp.order.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "shop_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    @Id
    private Long id;

    @Column("item_id")
    private long itemId;

    @Column("title")
    private String title;

    @Column("price")
    private long price;

    @Column("count")
    private int count;

    @Column("order_id")
    private Long orderId;
}
