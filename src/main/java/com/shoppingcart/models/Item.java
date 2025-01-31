package com.shoppingcart.models;

import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ErrorMessages;
import com.shoppingcart.exceptions.ItemValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public abstract class Item implements IItem {
    protected int id;
    protected int categoryId;
    protected int sellerId;
    protected BigDecimal price;
    protected int quantity;

    public Item(int itemId, int categoryId, int sellerId, BigDecimal price, int quantity) {
        validateId(itemId);
        validateQuantity(quantity);
        validatePrice(price);
        validateSellerId(sellerId);
        validateCategoryId(categoryId);
        this.id = itemId;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.quantity = quantity;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public int getSellerId() {
        return sellerId;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id &&
                categoryId == item.categoryId &&
                sellerId == item.sellerId &&
                Objects.equals(price, item.price);
    }

    private void validateId(int id) {
        if (id < 0) {
            throw new ItemValidationException(ErrorMessages.INVALID_ITEM_ID.format());
        }
    }

    protected void validateSellerId(int sellerId) {
        if (sellerId < 0) {
            throw new ItemValidationException(ErrorMessages.INVALID_SELLER_ID.format());
        }
        if (this instanceof VasItem) {
            if (sellerId != Config.VAS_ITEM_SELLER_ID) {
                throw new ItemValidationException(
                        ErrorMessages.INVALID_SELLER_ID_FOR_VAS.format(sellerId, Config.VAS_ITEM_SELLER_ID));
            }
        } else if (sellerId == Config.VAS_ITEM_SELLER_ID) {
            throw new ItemValidationException(
                    ErrorMessages.INVALID_SELLER_ID_FOR_NON_VAS.format(sellerId, this.getClass().getSimpleName(), Config.VAS_ITEM_SELLER_ID));
        }
    }

    protected void validateCategoryId(int categoryId) {
        if (categoryId < 0) {
            throw new ItemValidationException(ErrorMessages.INVALID_CATEGORY_ID.format());
        }
        if (categoryId == Category.DIGITAL_ITEM.getId() && !(this instanceof DigitalItem)) {
            throw new ItemValidationException(ErrorMessages.INVALID_CATEGORY_FOR_DIGITAL.format(
                    this.getClass().getSimpleName(), Category.DIGITAL_ITEM.getId()));
        } else if (categoryId == Category.VAS_ITEM.getId() && !(this instanceof VasItem)) {
            throw new ItemValidationException(ErrorMessages.INVALID_CATEGORY_FOR_VAS.format(
                    this.getClass().getSimpleName(), Category.VAS_ITEM.getId()));
        } else if (this instanceof VasItem && categoryId != Category.VAS_ITEM.getId()) {
            throw new ItemValidationException(ErrorMessages.INVALID_CATEGORY_FOR_VAS_ITEM.format(
                    categoryId, Category.VAS_ITEM.getId()));
        }
    }

    protected void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ItemValidationException(ErrorMessages.INVALID_QUANTITY.format());
        }
        if (quantity > Config.MAX_QUANTITY_PER_ITEM) {
            throw new ItemValidationException(ErrorMessages.MAX_QUANTITY_EXCEEDED.format(Config.MAX_QUANTITY_PER_ITEM));
        }
    }

    protected void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new ItemValidationException(ErrorMessages.PRICE_CANNOT_BE_NULL.format());
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ItemValidationException(ErrorMessages.PRICE_CANNOT_BE_NEGATIVE.format());
        }
        if (price.compareTo(Config.MAX_TOTAL_AMOUNT) > 0) {
            throw new ItemValidationException(ErrorMessages.PRICE_EXCEEDS_MAX.format(Config.MAX_TOTAL_AMOUNT));
        }
    }
}