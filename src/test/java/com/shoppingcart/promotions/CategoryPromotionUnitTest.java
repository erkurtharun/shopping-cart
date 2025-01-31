package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.PromotionType;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.models.IItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryPromotionUnitTest {
    private ICart mockCart;
    private IPromotion categoryPromotion;

    @BeforeEach
    public void setUp() {
        categoryPromotion = new CategoryPromotion();
        mockCart = mock(ICart.class);
    }

    @Test
    public void shouldReturnCorrectPromotionId() {
        // GIVEN: A CategoryPromotion instance
        int expectedPromotionId = PromotionType.CATEGORY.id;

        // WHEN: Retrieving the promotion ID
        int actualPromotionId = categoryPromotion.getId();

        // THEN: The promotion ID should match the expected value
        assertEquals(expectedPromotionId, actualPromotionId);
    }

    @Test
    public void shouldReturnFalse_WhenCartHasNoItems() {
        // GIVEN: An empty cart
        when(mockCart.getItems()).thenReturn(Map.of());

        // WHEN: Checking if promotion is applicable
        boolean result = categoryPromotion.isApplicable(mockCart);

        // THEN: The promotion should not be applicable
        assertFalse(result);
    }

    @Test
    public void shouldReturnFalse_WhenItemIsNotInDiscountCategories() {
        // GIVEN: A cart with an item not in the discount categories
        IItem nonDiscountItem = new DefaultItem(1, 0, 1, BigDecimal.valueOf(100), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, nonDiscountItem));

        // WHEN: Checking if promotion is applicable
        boolean result = categoryPromotion.isApplicable(mockCart);

        // THEN: The promotion should not be applicable
        assertFalse(result);
    }

    @Test
    public void shouldReturnTrue_WhenItemIsInDiscountCategories() {
        // GIVEN: A cart with an item in the discount category
        int discountCategoryId = Config.DISCOUNT_CATEGORIES.keySet().iterator().next();
        IItem discountItem = new DefaultItem(2, discountCategoryId, 1, BigDecimal.valueOf(100), 1);
        when(mockCart.getItems()).thenReturn(Map.of(2, discountItem));

        // WHEN: Checking if promotion is applicable
        boolean result = categoryPromotion.isApplicable(mockCart);

        // THEN: The promotion should be applicable
        assertTrue(result);
    }

    @Test
    public void shouldCalculateCorrectPromotionAmount() {
        // GIVEN: A cart with an eligible item for discount
        int discountCategoryId = Config.DISCOUNT_CATEGORIES.keySet().iterator().next();
        BigDecimal price = BigDecimal.valueOf(100);
        IItem discountItem = new DefaultItem(2, discountCategoryId, 1, price, 1);
        when(mockCart.getItems()).thenReturn(Map.of(2, discountItem));
        when(mockCart.getNonVasTotalPrice()).thenReturn(price);

        // WHEN: Calculating the promotion discount
        BigDecimal discount = categoryPromotion.calculatePromotion(mockCart);

        // THEN: The discount should be correctly calculated
        BigDecimal expectedDiscount = price.multiply(Config.DISCOUNT_CATEGORIES.get(discountCategoryId))
                .setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscount, discount);
    }

    @Test
    public void shouldReturnZeroDiscount_WhenNoApplicableItems() {
        // GIVEN: A cart with only non-discountable items
        IItem nonDiscountItem = new DefaultItem(1, 0, 1, BigDecimal.valueOf(100), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, nonDiscountItem));

        // WHEN: Calculating the promotion discount
        BigDecimal discount = categoryPromotion.calculatePromotion(mockCart);

        // THEN: The discount should be zero
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    public void shouldCalculateDiscountCorrectly_WithNewItem() {
        // GIVEN: A cart with an existing item and a new item eligible for discount
        IItem existingItem = new DefaultItem(1, 1001, 1, BigDecimal.valueOf(100), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, existingItem));

        int discountCategoryId = Config.DISCOUNT_CATEGORIES.keySet().iterator().next();
        BigDecimal priceOfNewItem = BigDecimal.valueOf(200);
        IItem newItem = new DefaultItem(2, discountCategoryId, 1, priceOfNewItem, 1);

        // WHEN: Calculating the promotion discount with the new item
        BigDecimal discount = categoryPromotion.calculatePromotionWithNewItem(mockCart, newItem);

        // THEN: The discount should be based on the new item price
        BigDecimal expectedDiscount = priceOfNewItem.multiply(Config.DISCOUNT_CATEGORIES.get(discountCategoryId))
                .setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscount, discount);
    }

    @Test
    public void shouldNotIncreaseDiscount_WhenNewItemIsNotEligible() {
        // GIVEN: A cart with a discount-eligible item
        BigDecimal price = BigDecimal.valueOf(100);
        int discountCategoryId = Config.DISCOUNT_CATEGORIES.keySet().iterator().next();
        IItem existingItem = new DefaultItem(1, discountCategoryId, 1, price, 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, existingItem));

        // AND: A new item that is not in the discount category
        IItem newItem = new DefaultItem(2, 0, 1, BigDecimal.valueOf(200), 1);

        // WHEN: Calculating the promotion discount with the new item
        BigDecimal discount = categoryPromotion.calculatePromotionWithNewItem(mockCart, newItem);

        // THEN: The discount should remain the same based on the existing item
        BigDecimal expectedDiscount = price.multiply(Config.DISCOUNT_CATEGORIES.get(discountCategoryId))
                .setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscount, discount);
    }
}