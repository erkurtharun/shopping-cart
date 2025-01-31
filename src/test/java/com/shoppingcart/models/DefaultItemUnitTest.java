package com.shoppingcart.models;

import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ItemValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultItemUnitTest extends ItemUnitTest<DefaultItem> {

    @Override
    protected DefaultItem createItem(int itemId, BigDecimal price, int quantity) {
        return new DefaultItem(itemId, Category.FURNITURE.getId(), 1, price, quantity);
    }

    @Override
    protected int getMaxQuantity() {
        return Config.MAX_QUANTITY_PER_ITEM;
    }

    @Test
    public void testDefaultItemCreation() {
        DefaultItem defaultItem = createItem(1, BigDecimal.valueOf(100.0), 1);
        assertEquals(1, defaultItem.getId());
        assertEquals(Category.FURNITURE.getId(), defaultItem.getCategoryId());
        assertEquals(1, defaultItem.getSellerId());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP), defaultItem.getPrice());
        assertEquals(1, defaultItem.getQuantity());
    }

    @Test
    public void testCreateDefaultItemWithInvalidCategoryId() {
        assertThrows(ItemValidationException.class, () -> new DefaultItem(1, Category.DIGITAL_ITEM.getId(), 1, BigDecimal.valueOf(100.0), 1));
        assertThrows(ItemValidationException.class, () -> new DefaultItem(1, Category.VAS_ITEM.getId(), 1, BigDecimal.valueOf(100.0), 1));
    }

    @Test
    public void testCreateDefaultItemWithInvalidSellerId() {
        assertThrows(ItemValidationException.class, () -> new DefaultItem(1, Category.FURNITURE.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(100.0), 1));
    }

    @Test
    public void testAddVasItem() {
        DefaultItem defaultItem = createItem(1, BigDecimal.valueOf(100.0), 1);
        VasItem vasItem = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(50.0), 1);

        defaultItem.addVasItem(vasItem);
        assertEquals(1, defaultItem.getVasItems().size());
        assertEquals(vasItem, defaultItem.getVasItems().getFirst());
    }

    @Test
    public void testAddVasItemWithDifferentItemId() {
        DefaultItem defaultItem = createItem(1, BigDecimal.valueOf(100.0), 1);
        VasItem vasItem = new VasItem(2, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(50.0), 1);

        assertThrows(ItemValidationException.class, () -> defaultItem.addVasItem(vasItem));
    }

    @Test
    public void testAddVasItemExceedingLimit() {
        DefaultItem defaultItem = createItem(1, BigDecimal.valueOf(100.0), 1);
        VasItem vasItem = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(50.0), Config.MAX_VAS_ITEMS_PER_DEFAULT_ITEM);
        defaultItem.addVasItem(vasItem);

        VasItem vasItem2 = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(50.0), 1);

        assertThrows(ItemValidationException.class, () -> defaultItem.addVasItem(vasItem2));
    }

    @Test
    public void testAddVasItemInvalidCategory() {
        DefaultItem defaultItem = new DefaultItem(1, 1, 1, BigDecimal.valueOf(100.0), 1);
        VasItem vasItem = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(50.0), 1);

        assertThrows(ItemValidationException.class, () -> defaultItem.addVasItem(vasItem));
    }

    @Test
    public void testAddSameVasItem() {
        DefaultItem defaultItem = createItem(1, BigDecimal.valueOf(100.0), 1);
        VasItem vasItem = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(50.0), 1);
        defaultItem.addVasItem(vasItem);

        VasItem differentVasItem = new VasItem(1, 3, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(60.0), 1);
        defaultItem.addVasItem(differentVasItem);

        VasItem sameVasItem = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(50.0), 1);
        defaultItem.addVasItem(sameVasItem);

        assertEquals(2, defaultItem.getVasItems().size());
        assertEquals(2, defaultItem.getVasItems().getFirst().getQuantity());
        assertEquals(1, defaultItem.getVasItems().getLast().getQuantity());
    }


    @Test
    public void testAddVasItemHigherPrice() {
        DefaultItem defaultItem = createItem(1, BigDecimal.valueOf(100.0), 1);
        VasItem vasItem = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(150.0), 1);

        assertThrows(ItemValidationException.class, () -> defaultItem.addVasItem(vasItem));
    }

    @Test
    public void testEquals_EqualObjects() {
        DefaultItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        DefaultItem item2 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        assertEquals(item1, item2); // Different objects but same content
    }

    @Test
    public void testEquals_DifferentIds() {
        DefaultItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        DefaultItem item2 = new DefaultItem(2, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        assertNotEquals(item1, item2); // Different ids
    }

    @Test
    public void testEquals_DifferentCategoryIds() {
        DefaultItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        DefaultItem item2 = new DefaultItem(1, Category.ELECTRONICS.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        assertNotEquals(item1, item2); // Different category ids
    }

    @Test
    public void testEquals_DifferentSellerIds() {
        DefaultItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        DefaultItem item2 = new DefaultItem(1, Category.FURNITURE.getId(), 1002, BigDecimal.valueOf(100.0), 1);
        assertNotEquals(item1, item2); // Different seller ids
    }

    @Test
    public void testEquals_DifferentPrices() {
        DefaultItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        DefaultItem item2 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(200.0), 1);
        assertNotEquals(item1, item2); // Different prices
    }

    @Test
    public void testEquals_NullObject() {
        DefaultItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 1001, BigDecimal.valueOf(100.0), 1);
        assertNotEquals(null, item1); // Comparing with null
    }
}
