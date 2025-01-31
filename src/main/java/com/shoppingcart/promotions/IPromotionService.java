package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.models.IItem;

public interface IPromotionService {
    PromotionResult calculateBestPromotion(ICart cart);

    PromotionResult calculateBestPromotionWithNewItem(ICart cart, IItem newItem);
}