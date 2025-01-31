package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.constants.PromotionType;
import com.shoppingcart.constants.TotalPricePromotionRange;
import com.shoppingcart.models.IItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TotalPricePromotion implements IPromotion {

    private final int id;

    public TotalPricePromotion() {
        this.id = PromotionType.TOTAL_PRICE.id;
    }

    @Override
    public boolean isApplicable(ICart cart) {
        BigDecimal totalPrice = cart.getTotalPrice();
        return TotalPricePromotionRange.getRange(totalPrice) != null;
    }

    @Override
    public BigDecimal calculatePromotion(ICart cart) {
        TotalPricePromotionRange range = TotalPricePromotionRange.getRange(cart.getTotalPrice());
        if (range == null) {
            return BigDecimal.ZERO;
        }
        return range.getDiscount().setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculatePromotionWithNewItem(ICart cart, IItem newItem) {
        TotalPricePromotionRange range = TotalPricePromotionRange.getRange(cart.getTotalPrice().add(newItem.getPrice()));
        if (range == null) {
            return BigDecimal.ZERO;
        }
        return range.getDiscount().setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getId() {
        return id;
    }
}