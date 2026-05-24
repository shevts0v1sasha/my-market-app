package ru.yandex.marketapp.cart.domain;

import lombok.Getter;

@Getter
public class CartItem {

    private final long itemId;
    private int amount;

    public CartItem(long itemId, int amount) {
        if (itemId < 1) {
            throw new IllegalArgumentException("Item id must be positive");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be >= 0");
        }
        this.itemId = itemId;
        this.amount = amount;
    }

    public void increaseAmount() {
        amount++;
    }

    public boolean decreaseAmount() {
        if (amount == 0) {
            return false;
        }
        amount--;
        return true;
    }
}
