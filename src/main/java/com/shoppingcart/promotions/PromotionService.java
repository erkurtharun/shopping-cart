package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.exceptions.ErrorMessages;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.IItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PromotionService implements IPromotionService {

    private final List<IPromotion> promotions;

    public PromotionService(List<IPromotion> promotions) {
        this.promotions = promotions;
    }

    @Override
    public PromotionResult calculateBestPromotion(ICart cart) {
        validateCart(cart);

        BigDecimal maxDiscount = BigDecimal.ZERO;
        int bestPromotionId = -1;

        for (IPromotion promotion : promotions) {
            BigDecimal discount = promotion.calculatePromotion(cart);
            if (discount.compareTo(maxDiscount) > 0) {
                maxDiscount = discount;
                bestPromotionId = promotion.getId();
            }
        }
        return new PromotionResult(maxDiscount.setScale(2, RoundingMode.HALF_UP), bestPromotionId);
    }

    @Override
    public PromotionResult calculateBestPromotionWithNewItem(ICart cart, IItem newItem) {
        validateCart(cart);
        validateItem(newItem);

        int bestPromotionId = -1;
        BigDecimal maxDiscount = BigDecimal.ZERO;
        for (IPromotion promotion : promotions) {
            BigDecimal discount = promotion.calculatePromotionWithNewItem(cart, newItem);
            if (discount.compareTo(maxDiscount) > 0) {
                maxDiscount = discount;
                bestPromotionId = promotion.getId();
            }
        }
        return new PromotionResult(maxDiscount.setScale(2, RoundingMode.HALF_UP), bestPromotionId);
    }

    private void validateCart(ICart cart) {
        if (cart == null) {
            throw new ItemValidationException(ErrorMessages.CART_CANNOT_BE_NULL.format());
        }
        if (cart.getItems() == null) {
            throw new ItemValidationException(ErrorMessages.CART_ITEMS_CANNOT_BE_NULL.format());
        }
    }

    private void validateItem(IItem item) {
        if (item == null) {
            throw new ItemValidationException(ErrorMessages.ITEM_CANNOT_BE_NULL.format());
        }
    }
}