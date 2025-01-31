package com.shoppingcart.models;

import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ItemValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class VasItemUnitTest extends ItemUnitTest<VasItem> {

    @Override
    protected VasItem createItem(int itemId, BigDecimal price, int quantity) {
        return new VasItem(1, itemId, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, price, quantity);
    }

    @Override
    protected int getMaxQuantity() {
        return Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM;
    }

    @Test
    public void testVasItemCreation() {
        VasItem vasItem = new VasItem(1, 1, BigDecimal.valueOf(100.0), 1);
        assertEquals(1, vasItem.getId());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP), vasItem.getPrice());
        assertEquals(1, vasItem.getQuantity());
        assertEquals(1, vasItem.getItemId());
    }

    @Test
    public void testVasItemInvalidQuantity() {
        assertThrows(ItemValidationException.class, () -> createItem(1, BigDecimal.valueOf(100.0), Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM + 1));
    }

    @Test
    public void testVasItemInvalidCategory() {
        assertThrows(ItemValidationException.class, () -> new VasItem(1, 1, Category.VAS_ITEM.getId() + 1, Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(100.0), 1));
    }

    @Test
    public void testVasItemInvalidSeller() {
        assertThrows(ItemValidationException.class, () -> new VasItem(1, 1, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID+1, BigDecimal.valueOf(100.0), 1));
    }
}
