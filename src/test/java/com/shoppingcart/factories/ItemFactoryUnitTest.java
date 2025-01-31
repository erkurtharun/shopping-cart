package com.shoppingcart.factories;

import com.shoppingcart.constants.Category;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.models.DigitalItem;
import com.shoppingcart.models.IItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class ItemFactoryUnitTest {

    private ItemFactory itemFactory;

    @BeforeEach
    public void setUp() {
        itemFactory = ItemFactory.getInstance();
    }

    @Test
    public void testCreateDigitalItem() {
        // GIVEN: A digital item with predefined attributes
        int itemId = 1;
        int sellerId = 100;
        BigDecimal price = BigDecimal.valueOf(50.0);
        int quantity = 2;
        int categoryId = Category.DIGITAL_ITEM.getId();

        // WHEN: The digital item is created using ItemFactory
        IItem item = itemFactory.createItem(itemId, categoryId, sellerId, price, quantity);

        // THEN: The created item should be a DigitalItem with the correct attributes
        assertNotNull(item);
        assertInstanceOf(DigitalItem.class, item);
        assertEquals(itemId, item.getId());
        assertEquals(categoryId, item.getCategoryId());
        assertEquals(sellerId, item.getSellerId());
        assertEquals(price.setScale(2, RoundingMode.HALF_UP), item.getPrice());
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    public void testCreateDefaultItem() {
        // GIVEN: A default item with predefined attributes
        int itemId = 2;
        int categoryId = 200;
        int sellerId = 101;
        BigDecimal price = BigDecimal.valueOf(100.0);
        int quantity = 3;

        // WHEN: The default item is created using ItemFactory
        IItem item = itemFactory.createItem(itemId, categoryId, sellerId, price, quantity);

        // THEN: The created item should be a DefaultItem with the correct attributes
        assertNotNull(item);
        assertInstanceOf(DefaultItem.class, item);
        assertEquals(itemId, item.getId());
        assertEquals(categoryId, item.getCategoryId());
        assertEquals(sellerId, item.getSellerId());
        assertEquals(price.setScale(2, RoundingMode.HALF_UP), item.getPrice());
        assertEquals(quantity, item.getQuantity());
    }
}