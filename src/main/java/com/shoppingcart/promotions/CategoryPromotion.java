package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.PromotionType;
import com.shoppingcart.models.IItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class CategoryPromotion implements IPromotion {

    private final int id;

    public CategoryPromotion() {
        this.id = PromotionType.CATEGORY.id;
    }

    @Override
    public boolean isApplicable(ICart cart) {
        return cart.getItems().values().stream().anyMatch(item -> Config.DISCOUNT_CATEGORIES.containsKey(item.getCategoryId()));
    }

    @Override
    public BigDecimal calculatePromotion(ICart cart) {
        return calculateDiscount(cart.getItems(), BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculatePromotionWithNewItem(ICart cart, IItem newItem) {
        BigDecimal newItemDiscount = BigDecimal.ZERO;
        if (Config.DISCOUNT_CATEGORIES.containsKey(newItem.getCategoryId())) {
            newItemDiscount = newItem.getPrice().multiply(Config.DISCOUNT_CATEGORIES.get(newItem.getCategoryId()));
        }

        return calculateDiscount(cart.getItems(), newItemDiscount);
    }

    @Override
    public int getId() {
        return id;
    }

    private BigDecimal calculateDiscount(Map<Integer, IItem> items, BigDecimal discount) {
        for (IItem item : items.values()) {
            BigDecimal discountRate = Config.DISCOUNT_CATEGORIES.get(item.getCategoryId());
            if (discountRate != null) {
                BigDecimal itemTotalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                discount = discount.add(itemTotalPrice.multiply(discountRate));
            }
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

}