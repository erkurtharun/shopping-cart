package com.shoppingcart.constants;

public enum PromotionType {
    SAME_SELLER(9909),
    CATEGORY(5676),
    TOTAL_PRICE(1232);

    public final int id;

    PromotionType(int id) {
        this.id = id;
    }
}
