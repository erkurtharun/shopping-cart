package com.shoppingcart.factories;

import com.shoppingcart.constants.Category;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.models.DigitalItem;
import com.shoppingcart.models.IItem;

import java.math.BigDecimal;

public class ItemFactory {

    private static volatile ItemFactory instance;

    private ItemFactory() {
        // Private constructor to prevent instantiation
    }

    public static ItemFactory getInstance() {
        if (instance == null) {
            synchronized (ItemFactory.class) {
                if (instance == null) {
                    instance = new ItemFactory();
                }
            }
        }
        return instance;
    }

    public IItem createItem(int itemId, int categoryId, int sellerId, BigDecimal price, int quantity) {
        if (categoryId == Category.DIGITAL_ITEM.getId()) {
            return new DigitalItem(itemId, sellerId, price, quantity);
        } else {
            return new DefaultItem(itemId, categoryId, sellerId, price, quantity);
        }
    }
}