package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.IItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PromotionServiceUnitTest {
    private PromotionService promotionService;
    private ICart mockCart;
    private IPromotion mockCategoryPromotion;
    private IPromotion mockTotalPricePromotion;
    private IPromotion mockSameSellerPromotion;

    @BeforeEach
    public void setUp() {
        mockCategoryPromotion = mock(IPromotion.class);
        mockTotalPricePromotion = mock(IPromotion.class);
        mockSameSellerPromotion = mock(IPromotion.class);

        promotionService = new PromotionService(List.of(mockCategoryPromotion, mockTotalPricePromotion, mockSameSellerPromotion));
        mockCart = mock(ICart.class);
    }

    @Test
    public void testNoItems_NoPromotion() {
        // Given
        when(mockCart.getItems()).thenReturn(Map.of());
        when(mockCategoryPromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.ZERO);
        when(mockTotalPricePromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.ZERO);
        when(mockSameSellerPromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.ZERO);

        // When
        PromotionResult result = promotionService.calculateBestPromotion(mockCart);

        // Then
        assertEquals(0, result.discount().compareTo(BigDecimal.ZERO), "No discount should be applied if there are no items.");
        verify(mockCategoryPromotion, times(1)).calculatePromotion(mockCart);
        verify(mockTotalPricePromotion, times(1)).calculatePromotion(mockCart);
        verify(mockSameSellerPromotion, times(1)).calculatePromotion(mockCart);
    }

    @Test
    public void testNoPromotionsApplicable() {
        // Given
        IItem item = mock(IItem.class);
        when(mockCart.getItems()).thenReturn(Map.of(1, item));
        when(mockCategoryPromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.ZERO);
        when(mockTotalPricePromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.ZERO);
        when(mockSameSellerPromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.ZERO);

        // When
        PromotionResult result = promotionService.calculateBestPromotion(mockCart);

        // Then
        assertEquals(0, result.discount().compareTo(BigDecimal.ZERO), "No discount should be applied.");
    }

    @Test
    public void testBestPromotionWithNewItem_EmptyCart() {
        // Given
        IItem newItem = mock(IItem.class);
        when(mockCart.getItems()).thenReturn(Map.of());
        when(mockTotalPricePromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.valueOf(250.0));
        when(mockCategoryPromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.ZERO);
        when(mockSameSellerPromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.ZERO);
        when(mockTotalPricePromotion.getId()).thenReturn(1);

        // When
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(mockCart, newItem);

        // Then
        assertEquals(1, resultWithNewItem.promotionId(), "TotalPricePromotion should be the best promotion for an empty cart with the new item.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.valueOf(250.0)), "Total discount should be 250.0 with new item from TotalPricePromotion.");
    }

    @Test
    public void testNoPromotionWithNewItem_EmptyCart() {
        // Given
        IItem newItem = mock(IItem.class);
        when(mockCart.getItems()).thenReturn(Map.of());
        when(mockTotalPricePromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.ZERO);
        when(mockCategoryPromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.ZERO);
        when(mockSameSellerPromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.ZERO);

        // When
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(mockCart, newItem);

        // Then
        assertEquals(-1, resultWithNewItem.promotionId(), "No promotion should be applicable for the new item with low price.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.ZERO), "Total discount should be 0.0 with new item.");
    }

    @Test
    public void testCalculateBestPromotion_NullCart() {
        // Then
        assertThrows(ItemValidationException.class, () -> promotionService.calculateBestPromotion(null));
    }

    @Test
    public void testCalculateBestPromotion_NullCartItems() {
        // Given
        when(mockCart.getItems()).thenReturn(null);

        // When
        Exception exception = assertThrows(ItemValidationException.class, () -> promotionService.calculateBestPromotion(mockCart));

        // Then
        assertEquals("Cart items cannot be null.", exception.getMessage());
    }

    @Test
    public void testCalculateBestPromotionWithNewItem_NullCart() {
        // Given
        IItem newItem = mock(IItem.class);

        // Then
        assertThrows(ItemValidationException.class, () -> promotionService.calculateBestPromotionWithNewItem(null, newItem));
    }

    @Test
    public void testCalculateBestPromotionWithNewItem_NullCartItems() {
        // Given
        when(mockCart.getItems()).thenReturn(null);
        IItem newItem = mock(IItem.class);

        // When
        Exception exception = assertThrows(ItemValidationException.class, () -> promotionService.calculateBestPromotionWithNewItem(mockCart, newItem));

        // Then
        assertEquals("Cart items cannot be null.", exception.getMessage());
    }

    @Test
    public void testCalculateBestPromotionWithNewItem_NullItem() {
        // Given
        when(mockCart.getItems()).thenReturn(Map.of());

        // When
        Exception exception = assertThrows(ItemValidationException.class, () -> promotionService.calculateBestPromotionWithNewItem(mockCart, null));

        // Then
        assertEquals("Item cannot be null.", exception.getMessage());
    }

    @Test
    public void testCalculateBestPromotion() {
        // Given
        IItem item1 = mock(IItem.class);
        IItem item2 = mock(IItem.class);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(300.0));
        when(mockTotalPricePromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.valueOf(250.0));
        when(mockTotalPricePromotion.getId()).thenReturn(1);
        when(mockCategoryPromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.valueOf(200.0));
        when(mockCategoryPromotion.getId()).thenReturn(2);
        when(mockSameSellerPromotion.calculatePromotion(mockCart)).thenReturn(BigDecimal.valueOf(100.0));
        when(mockSameSellerPromotion.getId()).thenReturn(3);

        // When
        PromotionResult result = promotionService.calculateBestPromotion(mockCart);

        // Then
        assertEquals(1, result.promotionId(), "TotalPricePromotion should be the best promotion.");
        assertEquals(0, result.discount().compareTo(BigDecimal.valueOf(250.0)), "Total discount should be 250.0 from TotalPricePromotion as the best promotion.");
    }

    @Test
    public void testCalculateBestPromotionWithNewItem() {
        // Given
        IItem item1 = mock(IItem.class);
        IItem item2 = mock(IItem.class);
        IItem newItem = mock(IItem.class);
        when(mockCart.getItems()).thenReturn(Map.of(1, item1, 2, item2));
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(4000.0));
        when(mockCategoryPromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.valueOf(200.0));
        when(mockCategoryPromotion.getId()).thenReturn(2);
        when(mockTotalPricePromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.valueOf(500.0));
        when(mockTotalPricePromotion.getId()).thenReturn(1);
        when(mockSameSellerPromotion.calculatePromotionWithNewItem(mockCart, newItem)).thenReturn(BigDecimal.valueOf(100.0));
        when(mockSameSellerPromotion.getId()).thenReturn(3);

        // When
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(mockCart, newItem);

        // Then
        assertEquals(1, resultWithNewItem.promotionId(), "TotalPricePromotion should be the best promotion, as the new item triggers it.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.valueOf(500.0)), "Total discount should be 500.00 with new Item from TotalPricePromotion as the best promotion.");
    }
}
