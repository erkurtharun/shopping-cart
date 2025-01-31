package com.shoppingcart.models;

import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ItemValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DigitalItemUnitTest extends ItemUnitTest<DigitalItem> {

    @Override
    protected DigitalItem createItem(int itemId, BigDecimal price, int quantity) {
        return new DigitalItem(itemId, 1, price, quantity);
    }

    @Override
    protected int getMaxQuantity() {
        return Config.MAX_DIGITAL_ITEM_QUANTITY;
    }

    @Test
    public void testDigitalItemCreation() {
        DigitalItem digitalItem = createItem(1, BigDecimal.valueOf(100.0), 1);
        assertEquals(1, digitalItem.getId());
        assertEquals(Category.DIGITAL_ITEM.getId(), digitalItem.getCategoryId());
        assertEquals(1, digitalItem.getSellerId());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP), digitalItem.getPrice());
        assertEquals(1, digitalItem.getQuantity());
    }

    @Test
    public void testCreateDigitalItemWithInvalidSellerId() {
        assertThrows(ItemValidationException.class, () -> new DigitalItem(1, Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(100.0), 1));
    }
}