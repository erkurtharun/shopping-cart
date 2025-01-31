package com.shoppingcart.models;

import com.shoppingcart.config.Config;
import com.shoppingcart.exceptions.ItemValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ItemUnitTest<T extends Item> {

    protected abstract T createItem(int itemId, BigDecimal price, int quantity);
    protected abstract int getMaxQuantity();

    @Test
    public void testItemCreation() {
        T item = createItem(1, BigDecimal.valueOf(100.0), 1);
        assertEquals(1, item.getId());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP), item.getPrice());
        assertEquals(1, item.getQuantity());
    }

    @Test
    public void testItemInvalidId() {
        assertThrows(ItemValidationException.class, () -> createItem(-1, BigDecimal.valueOf(100.0), 1));
    }

    @Test
    public void TestItemNullPrice() {
        assertThrows(ItemValidationException.class, () -> createItem(1, null, 1));
    }

    @Test
    public void testItemZeroPrice() {
        assertDoesNotThrow(() -> createItem(1, BigDecimal.ZERO, 1));
    }

    @Test
    public void testItemNegativePrice() {
        assertThrows(ItemValidationException.class, () -> createItem(1, BigDecimal.valueOf(-100.0), 1));
    }

    @Test
    public void testItemOverMaxPrice() {
        assertThrows(ItemValidationException.class, () -> createItem(1, Config.MAX_TOTAL_AMOUNT.add(BigDecimal.ONE), 1));
    }

    @Test
    public void testItemZeroQuantity() {
        assertThrows(ItemValidationException.class, () -> createItem(1, BigDecimal.valueOf(100.0), 0));
    }

    @Test
    public void testItemMaximumQuantity() {
        T item = createItem(1, BigDecimal.valueOf(100.0), getMaxQuantity());
        assertEquals(getMaxQuantity(), item.getQuantity());
    }

    @Test
    public void testItemOverMaximumQuantity() {
        assertThrows(ItemValidationException.class, () -> createItem(1, BigDecimal.valueOf(100.0), getMaxQuantity() + 1));
    }

    @Test
    public void testItemSetQuantity() {
        T item = createItem(1, BigDecimal.valueOf(100.0), 1);

        item.setQuantity(getMaxQuantity() - 1);
        assertEquals(getMaxQuantity() -1, item.getQuantity());

        assertThrows(ItemValidationException.class, () -> item.setQuantity(-1));
        assertThrows(ItemValidationException.class, () -> item.setQuantity(0));
        assertThrows(ItemValidationException.class, () -> item.setQuantity(getMaxQuantity() + 1));
    }

    @Test
    public void testNegativeSellerId() {
        assertThrows(ItemValidationException.class, () -> new DefaultItem(1, 1, -1, BigDecimal.valueOf(100.0), 1));
    }

    @Test
    public void testNegativeCategoryId() {
        assertThrows(ItemValidationException.class, () -> new DefaultItem(1, -1, 1, BigDecimal.valueOf(100.0), 1));
    }

    @Test
    public void testItemEquality() {
        T item1 = createItem(1, BigDecimal.valueOf(100.0), 1);
        T item2 = createItem(1, BigDecimal.valueOf(100.0), 1);
        assertEquals(item1, item2);
    }

    @Test
    public void testItemInequality() {
        T item1 = createItem(1, BigDecimal.valueOf(100.0), 1);
        T item2 = createItem(2, BigDecimal.valueOf(100.0), 1);
        assertNotEquals(item1, item2);
    }
}