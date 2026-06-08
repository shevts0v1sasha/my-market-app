package ru.yandex.marketapp.cart.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class Cart {

    private final CartId id;
    private final List<CartItem> items;

    public Cart(CartId id, List<CartItem> items) {
        this.id = id;
        this.items = new ArrayList<>(items);
    }

    public void increaseItem(long itemId) {
        CartItem item = findItem(itemId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem(itemId, 0);
                    items.add(newItem);
                    return newItem;
                });
        item.increaseAmount();
    }

    public void decreaseItem(long itemId) {
        Optional<CartItem> optionalItem = findItem(itemId);
        if (optionalItem.isEmpty()) {
            return;
        }
        CartItem item = optionalItem.get();
        boolean decreased = item.decreaseAmount();
        if (decreased && item.getAmount() == 0) {
            items.remove(item);
        }
    }

    public void deleteItem(long itemId) {
        items.removeIf(i -> i.getItemId() == itemId);
    }

    public void clear() {
        items.clear();
    }

    public int countFor(long itemId) {
        return findItem(itemId)
                .map(CartItem::getAmount)
                .orElse(0);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private Optional<CartItem> findItem(long itemId) {
        return items.stream()
                .filter(i -> i.getItemId() == itemId)
                .findFirst();
    }
}
