package com.shoppingcart.testutil;

import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.models.DigitalItem;
import com.shoppingcart.models.IItem;
import com.shoppingcart.models.VasItem;

import java.math.BigDecimal;


public class TestUtils {

    public static IItem createDefaultItem(int itemId, int categoryId, int sellerId, double price, int quantity) {
        return new DefaultItem(itemId, categoryId, sellerId, BigDecimal.valueOf(price), quantity);
    }

    public static IItem createDigitalItem(int itemId, int sellerId, double price, int quantity) {
        return new DigitalItem(itemId, sellerId, BigDecimal.valueOf(price), quantity);
    }

    public static VasItem createVasItem(int itemId, int id, double price, int quantity) {
        return new VasItem(itemId, id, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(price), quantity);
    }

    public static DefaultItem createCategoryDiscountEligibleItem(int itemId, double price) {
        int discountCategoryId = Config.VALID_CATEGORIES_FOR_VAS.iterator().next();
        return (DefaultItem) TestUtils.createDefaultItem(itemId, discountCategoryId, 2001, price, 1);
    }
}