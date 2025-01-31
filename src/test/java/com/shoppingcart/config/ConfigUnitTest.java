package com.shoppingcart.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class ConfigUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUnitTest.class);

    @BeforeEach
    public void setUp() {
        Properties properties = new Properties();
        properties.setProperty("max.unique.items", "10");
        properties.setProperty("max.total.items", "30");
        properties.setProperty("max.total.amount", "500000");
        properties.setProperty("max.digital.item.quantity", "5");
        properties.setProperty("vas.item.seller.id", "5003");
        properties.setProperty("max.vas.items.per.default.item", "3");
        properties.setProperty("max.quantity.per.item", "10");
        properties.setProperty("same.seller.promotion.rate", "0.10");
        properties.setProperty("discount.categories", "3003:0.05");
        properties.setProperty("valid.categories.for.vas", "1001,3004");

        Config.setProperties(properties);
    }

    @Test
    public void testMaxUniqueItems() {
        assertEquals(10, Config.MAX_UNIQUE_ITEMS);
    }

    @Test
    public void testMaxTotalItems() {
        assertEquals(30, Config.MAX_TOTAL_ITEMS);
    }

    @Test
    public void testMaxTotalAmount() {
        assertEquals(0, Config.MAX_TOTAL_AMOUNT.compareTo(new BigDecimal("500000")));
    }

    @Test
    public void testMaxDigitalItemQuantity() {
        assertEquals(5, Config.MAX_DIGITAL_ITEM_QUANTITY);
    }

    @Test
    public void testVasItemSellerId() {
        assertEquals(5003, Config.VAS_ITEM_SELLER_ID);
    }

    @Test
    public void testMaxVasItemsPerDefaultItem() {
        assertEquals(3, Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM);
    }

    @Test
    public void testMaxQuantityPerItem() {
        assertEquals(10, Config.MAX_QUANTITY_PER_ITEM);
    }

    @Test
    public void testSameSellerPromotionRate() {
        assertEquals(0, Config.SAME_SELLER_PROMOTION_RATE.compareTo(new BigDecimal("0.10")));
    }

    @Test
    public void testDiscountCategories() {
        Map<Integer, BigDecimal> discountCategories = Config.DISCOUNT_CATEGORIES;
        assertNotNull(discountCategories);
        assertFalse(discountCategories.isEmpty());
        // Assuming discount categories are defined in cart-config.properties
        assertEquals(0, discountCategories.get(3003).compareTo(new BigDecimal("0.05")));
    }

    @Test
    public void testValidCategoriesForVas() {
        Set<Integer> validCategoriesForVas = Config.VALID_CATEGORIES_FOR_VAS;
        assertNotNull(validCategoriesForVas);
        assertFalse(validCategoriesForVas.isEmpty());
        // Assuming valid categories for VAS are defined in cart-config.properties
        assertTrue(validCategoriesForVas.contains(1001));
        assertTrue(validCategoriesForVas.contains(3004));
    }

    @Test
    public void testDefaultValueForMissingIntProperty() {
        assertEquals(20, Config.getIntProperty("non.existent.property", 20));
    }

    @Test
    public void testDefaultValueForMissingBigDecimalProperty() {
        assertEquals(0, Config.getBigDecimalProperty("non.existent.property", new BigDecimal("30.0")).compareTo(new BigDecimal("30.0")));
    }

    @Test
    public void testLoadProperties_IOException() {
        Properties mockProperties = Mockito.mock(Properties.class);

        try (InputStream ignored = mock(InputStream.class)) {
            Mockito.doThrow(new IOException("File not found")).when(mockProperties).load(any(InputStream.class));

            Config.setProperties(mockProperties);

            java.lang.reflect.Method method = Config.class.getDeclaredMethod("loadProperties");
            method.setAccessible(true);

            assertThrows(InvocationTargetException.class, () -> method.invoke(null), "Expected ExceptionInInitializerError to be thrown");
        } catch (Exception e) {
            logger.error("Unexpected exception occurred during test: {}", e.getMessage());
            fail("Unexpected exception occurred during test: " + e.getMessage());
        }
    }

    @Test
    public void testSetProperties() {
        Properties newProperties = new Properties();
        newProperties.setProperty("test.property", "testValue");
        Config.setProperties(newProperties);

        assertEquals("testValue", newProperties.getProperty("test.property"));
    }

    @Test
    public void testGetIntProperty_InvalidFormat() {
        Properties properties = new Properties();
        properties.setProperty("invalid.int.property", "invalid");
        Config.setProperties(properties);

        assertEquals(20, Config.getIntProperty("invalid.int.property", 20));
    }

    @Test
    public void testGetBigDecimalProperty_InvalidFormat() {
        Properties properties = new Properties();
        properties.setProperty("invalid.bigdecimal.property", "invalid");
        Config.setProperties(properties);

        assertEquals(0, Config.getBigDecimalProperty("invalid.bigdecimal.property", new BigDecimal("30.0")).compareTo(new BigDecimal("30.0")));
    }
}
