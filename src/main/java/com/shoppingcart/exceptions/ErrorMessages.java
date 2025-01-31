package com.shoppingcart.exceptions;

public enum ErrorMessages {
    ITEM_NOT_FOUND("Item with ID %d not found in the cart."),
    ITEM_VALIDATION_FAILED("Adding %d of item ID %d exceeds the max quantity per item. Current quantity: %d, attempted to add: %d, allowed limit: %d."),
    INVALID_VAS_ITEM_ASSOCIATION("VAS item must be associated with the same DefaultItem."),
    INVALID_VAS_ITEM_CATEGORY("VAS item cannot be added to this category."),
    INVALID_VAS_ITEM_PRICE("VAS item price cannot be greater than the DefaultItem price."),
    MAX_VAS_ITEMS_EXCEEDED("Cannot add more than %d VAS items to this DefaultItem."),
    INVALID_QUANTITY("Quantity must be greater than zero."),
    MAX_DIGITAL_ITEM_QUANTITY_EXCEEDED("Quantity cannot exceed %d for digital items."),
    INVALID_ITEM_ID("Item ID must be greater than or equal to zero."),
    INVALID_SELLER_ID("Seller ID must be greater than or equal to zero."),
    INVALID_SELLER_ID_FOR_VAS("Invalid sellerId: %d. VAS item must have the sellerId reserved for VAS items: %d."),
    INVALID_SELLER_ID_FOR_NON_VAS("Invalid sellerId: %d for item type: %s. Only VAS items can have the sellerId reserved for VAS items: %d."),
    INVALID_CATEGORY_ID("Category ID must be greater than or equal to zero."),
    INVALID_CATEGORY_FOR_DIGITAL("%s cannot have the categoryId reserved for Digital items: %d."),
    INVALID_CATEGORY_FOR_VAS("%s cannot have the categoryId reserved for VAS items: %d."),
    INVALID_CATEGORY_FOR_VAS_ITEM("Invalid categoryId: %d. VAS item must have the categoryId reserved for VAS items: %d."),
    MAX_QUANTITY_EXCEEDED("Quantity must be less than or equal to %d."),
    PRICE_CANNOT_BE_NULL("Price cannot be null."),
    PRICE_CANNOT_BE_NEGATIVE("Price cannot be negative."),
    PRICE_EXCEEDS_MAX("Price must be less than or equal to %s."),
    CART_CANNOT_BE_NULL("Cart cannot be null."),
    CART_ITEMS_CANNOT_BE_NULL("Cart items cannot be null."),
    ITEM_CANNOT_BE_NULL("Item cannot be null."),
    VAS_ITEM_CANNOT_BE_NULL("The VAS item to be added cannot be null."),
    PARENT_ITEM_NOT_FOUND("Parent item of vas item not found in the cart."),
    VAS_ITEM_MUST_BE_ASSOCIATED("VAS item to be added must be associated with a DefaultItem."),
    VAS_ITEM_CANNOT_BE_ADDED_DIRECTLY("VAS items cannot be added directly to the cart."),
    DIGITAL_ITEM_LIMIT_EXCEEDED("Adding %d digital items exceeds the allowed limit of %d. Current digital item count: %d."),
    TOTAL_ITEM_LIMIT_EXCEEDED("Adding %d items exceeds the total allowed limit. Current total: %d, attempted to add: %d, allowed limit: %d."),
    TOTAL_AMOUNT_EXCEEDED("Adding items causes total amount to exceed the allowed limit. Current total: %.2f, attempted to add: %.2f, resulting total: %.2f, allowed limit: %.2f."),
    UNIQUE_ITEM_LIMIT_EXCEEDED("Adding this item causes unique item count to exceed the allowed limit. Current unique item count: %d, allowed limit: %d.");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}