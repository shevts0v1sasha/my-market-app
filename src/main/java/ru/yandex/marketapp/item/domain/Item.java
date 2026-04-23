package ru.yandex.marketapp.item.domain;

import lombok.Getter;

@Getter
public class Item {
    private ItemId id;
    private String title;
    private String description;
    private String imgPath;
    private Price price;
    private int count;

    public Item(ItemId id, String title, String description, String imgPath, Price price, int count) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;

        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be < 0");
        }
        this.count = count;
    }
}
