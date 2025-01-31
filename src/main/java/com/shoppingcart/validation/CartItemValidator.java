package com.shoppingcart.validation;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ErrorMessages;
import com.shoppingcart.exceptions.ItemNotFoundException;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.models.IItem;
import com.shoppingcart.models.VasItem;

import java.math.BigDecimal;

public class CartItemValidator implements ICartItemValidator {

    public void validateItem(IItem item, ICart cart) {
        validateNotNull(item, cart);
        validateCategory(item, cart);
        validateTotalQuantity(item, cart);
        validateUniqueItemCount(item, cart);
        validateTotalAmount(item, cart);
    }

    public void validateVasItem(VasItem vasItem, ICart cart) {
        if (vasItem == null) {
            throw new ItemValidationException(ErrorMessages.VAS_ITEM_CANNOT_BE_NULL.format());
        }

        IItem parentItem = cart.getItems().get(vasItem.getItemId());
        if (parentItem == null) {
            throw new ItemNotFoundException(ErrorMessages.PARENT_ITEM_NOT_FOUND.format());
        }

        if (!(parentItem instanceof DefaultItem)) {
            throw new ItemValidationException(ErrorMessages.VAS_ITEM_MUST_BE_ASSOCIATED.format());
        }
        validateTotalAmount(vasItem, cart);
    }

    private void validateNotNull(Object item, ICart cart) {
        if (item == null) {
            throw new ItemValidationException(ErrorMessages.ITEM_CANNOT_BE_NULL.format());
        }
        if (cart == null) {
            throw new ItemValidationException(ErrorMessages.CART_CANNOT_BE_NULL.format());
        }
    }

    private void validateCategory(IItem item, ICart cart) {
        if (item instanceof VasItem) {
            throw new ItemValidationException(ErrorMessages.VAS_ITEM_CANNOT_BE_ADDED_DIRECTLY.format());
        }
        if (item.getCategoryId() == Category.DIGITAL_ITEM.getId() && !validateDigitalItem(item, cart)) {
            throw new ItemValidationException(ErrorMessages.DIGITAL_ITEM_LIMIT_EXCEEDED.format(
                    item.getQuantity(), Config.MAX_DIGITAL_ITEM_QUANTITY, cart.getDigitalItemCount()));
        }
    }

    private boolean validateDigitalItem(IItem item, ICart cart) {
        return cart.getDigitalItemCount() + item.getQuantity() <= Config.MAX_DIGITAL_ITEM_QUANTITY;
    }

    private void validateTotalQuantity(IItem item, ICart cart) {
        int newTotalQuantity = cart.getTotalItemCount() + item.getQuantity();
        if (newTotalQuantity > Config.MAX_TOTAL_ITEMS) {
            throw new ItemValidationException(ErrorMessages.TOTAL_ITEM_LIMIT_EXCEEDED.format(
                    item.getQuantity(), cart.getTotalItemCount(), item.getQuantity(), Config.MAX_TOTAL_ITEMS));
        }
    }

    private void validateTotalAmount(IItem item, ICart cart) {
        BigDecimal newTotalPrice = cart.getTotalPrice().add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        if (newTotalPrice.compareTo(Config.MAX_TOTAL_AMOUNT) <= 0) {
            return;
        }

        BigDecimal newTotalAmount = newTotalPrice.subtract(cart.calculateBestPromotionWithTempItem(item).discount());
        if (newTotalAmount.compareTo(Config.MAX_TOTAL_AMOUNT) > 0) {
            throw new ItemValidationException(ErrorMessages.TOTAL_AMOUNT_EXCEEDED.format(
                    cart.getTotalPrice(), item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())), newTotalAmount, Config.MAX_TOTAL_AMOUNT));
        }
    }

    private void validateUniqueItemCount(IItem item, ICart cart) {
        if (cart.getItems().containsKey(item.getId())) {
            return;
        }
        int newUniqueItemCount = cart.getUniqueItemCount() + 1;
        if (newUniqueItemCount > Config.MAX_UNIQUE_ITEMS) {
            throw new ItemValidationException(ErrorMessages.UNIQUE_ITEM_LIMIT_EXCEEDED.format(
                    cart.getUniqueItemCount(), Config.MAX_UNIQUE_ITEMS));
        }
    }
}