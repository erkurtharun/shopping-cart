package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.constants.PromotionType;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.testutil.TestUtils;
import com.shoppingcart.models.IItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SameSellerPromotionUnitTest {
    private ICart mockCart;
    private SameSellerPromotion sameSellerPromotion;

    @BeforeEach
    public void setUp() {
        sameSellerPromotion = new SameSellerPromotion();
        mockCart = mock(ICart.class);
    }

    @Test
    public void testSameSellerId(){
        assertEquals(PromotionType.SAME_SELLER.id, sameSellerPromotion.getId());
    }

    @Test
    public void testIsApplicable_SameSeller() {

        // Given
        IItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100), 1);
        IItem item2 = new DefaultItem(2, Category.ELECTRONICS.getId(), 2001, BigDecimal.valueOf(200), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));

        // When
        boolean result = sameSellerPromotion.isApplicable(mockCart);

        // Then
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_DifferentSellers() {

        // Given
        IItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100), 1);
        IItem item2 = new DefaultItem(2, Category.ELECTRONICS.getId(), 2002, BigDecimal.valueOf(200), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));

        // When
        boolean result = sameSellerPromotion.isApplicable(mockCart);

        // Then
        assertFalse(result);
    }

    @Test
    public void testIsApplicable_SingleItem() {
        // Given
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item));

        // When
        boolean result = sameSellerPromotion.isApplicable(mockCart);

        // Then
        assertFalse(result);
    }

    @Test
    public void testCalculatePromotion_SameSeller() {
        // Given
        IItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100), 1);
        IItem item2 = new DefaultItem(2, Category.ELECTRONICS.getId(), 2001, BigDecimal.valueOf(200), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(300));
        when(mockCart.getNonVasTotalPrice()).thenReturn(BigDecimal.valueOf(300));

        // When
        BigDecimal discount = sameSellerPromotion.calculatePromotion(mockCart);

        // Then
        BigDecimal expectedDiscount = BigDecimal.valueOf(300).multiply(Config.SAME_SELLER_PROMOTION_RATE).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscount, discount);
    }

    @Test
    public void testCalculatePromotion_DifferentSellers() {
        // Given
        IItem item1 = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100), 1);
        IItem item2 = new DefaultItem(2, Category.ELECTRONICS.getId(), 2002, BigDecimal.valueOf(200), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(300));
        when(mockCart.getNonVasTotalPrice()).thenReturn(BigDecimal.valueOf(300));

        // When
        BigDecimal discount = sameSellerPromotion.calculatePromotion(mockCart);

        // Then
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    public void testCalculatePromotion_SingleItem() {
        // Given
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100), 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(100));
        when(mockCart.getNonVasTotalPrice()).thenReturn(BigDecimal.valueOf(100));

        // When
        BigDecimal discount = sameSellerPromotion.calculatePromotion(mockCart);

        // Then
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    public void testCalculatePromotion_IncludeVasItems() {
        // Given
        IItem item1 = TestUtils.createDefaultItem(1, Category.ELECTRONICS.getId(), 2001, 100.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 1002, 2001, 200.0, 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(320));
        when(mockCart.getNonVasTotalPrice()).thenReturn(BigDecimal.valueOf(300));

        // When
        BigDecimal discount = sameSellerPromotion.calculatePromotion(mockCart);

        System.out.println("harun1" +discount);

        // Then
        assertEquals(0, discount.compareTo(BigDecimal.valueOf(30)), "Total discount should be 30.0 (10% of 300.0 excluding VAS item)");
    }

    @Test
    public void testCalculatePromotionWithNewItem() {
        // Given
        IItem item1 = TestUtils.createDefaultItem(1, Category.FURNITURE.getId(), 2001, 100.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 2002, 2001, 200.0, 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(300));
        when(mockCart.getNonVasTotalPrice()).thenReturn(BigDecimal.valueOf(300));

        // When
        BigDecimal discount = sameSellerPromotion.calculatePromotionWithNewItem(mockCart, TestUtils.createDefaultItem(3, 2001, 2001, 100.0, 1));

        // Then
        assertEquals(0, discount.compareTo(BigDecimal.valueOf(400).multiply(Config.SAME_SELLER_PROMOTION_RATE).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    public void testCalculatePromotionWithNewItem_DifferentSeller() {
        // Given
        IItem item1 = TestUtils.createDefaultItem(1, Category.FURNITURE.getId(), 2001, 100.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 2002, 2001, 200.0, 1);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(300));
        when(mockCart.getNonVasTotalPrice()).thenReturn(BigDecimal.valueOf(300));

        // When
        BigDecimal discount = sameSellerPromotion.calculatePromotionWithNewItem(mockCart, TestUtils.createDefaultItem(3, 2002, 2002, 100.0, 1));

        // Then
        assertEquals(BigDecimal.ZERO, discount);
    }
}