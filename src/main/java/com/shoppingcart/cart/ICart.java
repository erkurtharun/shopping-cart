package com.shoppingcart.cart;

import com.shoppingcart.models.IItem;
import com.shoppingcart.models.VasItem;
import com.shoppingcart.promotions.PromotionResult;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

public interface ICart {
    Map<Integer, IItem> getItems();

    BigDecimal getTotalAmount();

    BigDecimal getTotalPrice();

    BigDecimal getNonVasTotalPrice();

    BigDecimal getTotalDiscount();

    int getUniqueItemCount();

    int getTotalItemCount();

    int getDigitalItemCount();

    int getAppliedPromotionId();

    void addItem(IItem item);

    void addVasItem(VasItem item);

    void removeItem(int itemId);

    void reset();

    JSONObject display();

    PromotionResult calculateBestPromotionWithTempItem(IItem item);

    void applyPromotions();
}