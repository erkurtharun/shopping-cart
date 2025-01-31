package com.shoppingcart.models;

import java.math.BigDecimal;

import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ErrorMessages;
import com.shoppingcart.exceptions.ItemValidationException;

public class DigitalItem extends Item {
    public DigitalItem(int itemId, int sellerId, BigDecimal price, int quantity) {
        super(itemId, Category.DIGITAL_ITEM.getId(), sellerId, price, quantity);
    }

    @Override
    public void setQuantity(int quantity) {
        super.setQuantity(quantity);
    }

    @Override
    protected void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ItemValidationException(ErrorMessages.INVALID_QUANTITY.format());
        }
        if (quantity > Config.MAX_DIGITAL_ITEM_QUANTITY) {
            throw new ItemValidationException(
                    ErrorMessages.MAX_DIGITAL_ITEM_QUANTITY_EXCEEDED.format(Config.MAX_DIGITAL_ITEM_QUANTITY));
        }
    }
}