package com.shoppingcart.models;

import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ErrorMessages;
import com.shoppingcart.exceptions.ItemValidationException;

import java.math.BigDecimal;

public class VasItem extends Item {
    private final int itemId;

    public VasItem(int itemId, int id, int categoryId, int sellerId, BigDecimal price, int quantity) {
        super(id, categoryId, sellerId, price, quantity);
        this.itemId = itemId;
    }

    public VasItem(int itemId, int id, BigDecimal price, int quantity) {
        super(id, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, price, quantity);
        this.itemId = itemId;
    }

    @Override
    protected void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ItemValidationException(ErrorMessages.INVALID_QUANTITY.format());
        }
        if (quantity > Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM) {
            throw new ItemValidationException(ErrorMessages.MAX_QUANTITY_EXCEEDED.format(Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM));
        }
    }

    public int getItemId() {
        return itemId;
    }
}