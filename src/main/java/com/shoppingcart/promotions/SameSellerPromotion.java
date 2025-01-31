package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.PromotionType;
import com.shoppingcart.models.IItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SameSellerPromotion implements IPromotion {

    private final int id;

    public SameSellerPromotion() {
        this.id = PromotionType.SAME_SELLER.id;
    }

    @Override
    public boolean isApplicable(ICart cart) {
        return areAllItemsFromSameSeller(cart.getItems().values());
    }

    @Override
    public BigDecimal calculatePromotion(ICart cart) {
        if (!isApplicable(cart)) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalDiscountablePrice = cart.getNonVasTotalPrice();
        return totalDiscountablePrice.multiply(Config.SAME_SELLER_PROMOTION_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculatePromotionWithNewItem(ICart cart, IItem newItem) {

        List<IItem> tempItems = new ArrayList<>(cart.getItems().values());
        tempItems.add(newItem);
        if (areAllItemsFromSameSeller(tempItems)) {
            BigDecimal totalDiscountablePrice = cart.getNonVasTotalPrice().add(newItem.getPrice());
            return totalDiscountablePrice.multiply(Config.SAME_SELLER_PROMOTION_RATE).setScale(2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    private boolean areAllItemsFromSameSeller(Collection<IItem> items) {
        if (items.size() < 2) {
            return false;
        }

        int sellerId = items.iterator().next().getSellerId();
        for (IItem item : items) {
            if (item.getSellerId() != sellerId) {
                return false;
            }
        }

        return true;
    }

}