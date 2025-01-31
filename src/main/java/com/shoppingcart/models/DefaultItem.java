package com.shoppingcart.models;

import java.math.BigDecimal;
import com.shoppingcart.config.Config;
import com.shoppingcart.exceptions.ErrorMessages;
import com.shoppingcart.exceptions.ItemValidationException;

import java.util.ArrayList;
import java.util.List;

public class DefaultItem extends Item {
    private final List<VasItem> vasItems = new ArrayList<>();

    public DefaultItem(int itemId, int categoryId, int sellerId, BigDecimal price, int quantity) {
        super(itemId, categoryId, sellerId, price, quantity);
    }

    public void addVasItem(VasItem vasItem) {
        validateVasItem(vasItem);

        for (VasItem existingVas : vasItems) {
            if (existingVas.equals(vasItem)) {
                int newQuantity = existingVas.getQuantity() + vasItem.getQuantity();
                existingVas.setQuantity(newQuantity);
                return;
            }
        }

        vasItems.add(vasItem);
    }

    public List<VasItem> getVasItems() {
        return vasItems;
    }

    public void validateVasItem(VasItem vasItem) {
        int currentVasItemCount = vasItems.stream()
                .mapToInt(IItem::getQuantity)
                .sum();

        if (vasItem.getItemId() != this.id) {
            throw new ItemValidationException(ErrorMessages.INVALID_VAS_ITEM_ASSOCIATION.format());
        }

        if (!Config.VALID_CATEGORIES_FOR_VAS.contains(this.categoryId)) {
            throw new ItemValidationException(ErrorMessages.INVALID_VAS_ITEM_CATEGORY.format());
        }

        if (vasItem.getPrice().compareTo(this.price) > 0) {
            throw new ItemValidationException(ErrorMessages.INVALID_VAS_ITEM_PRICE.format());
        }

        if (currentVasItemCount + vasItem.getQuantity() > Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM) {
            throw new ItemValidationException(
                    ErrorMessages.MAX_VAS_ITEMS_EXCEEDED.format(Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM));
        }
    }
}