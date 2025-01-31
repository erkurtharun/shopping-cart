package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.models.IItem;

import java.math.BigDecimal;

public interface IPromotion {
    boolean isApplicable(ICart cart);

    BigDecimal calculatePromotion(ICart cart);

    BigDecimal calculatePromotionWithNewItem(ICart cart, IItem newItem);

    int getId();
}
