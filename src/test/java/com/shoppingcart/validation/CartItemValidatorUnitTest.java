package com.shoppingcart.validation;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ItemNotFoundException;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.*;
import com.shoppingcart.promotions.PromotionResult;
import com.shoppingcart.testutil.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartItemValidatorUnitTest {

    private CartItemValidator validator;
    private ICart mockCart;

    @BeforeEach
    public void setUp() {
        validator = new CartItemValidator();
        mockCart = mock(ICart.class);

        // Default behavior for mockCart
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.ZERO);
        when(mockCart.getTotalDiscount()).thenReturn(BigDecimal.ZERO);
        when(mockCart.getItems()).thenReturn(Map.of());
    }

    @Test
    public void testValidateItem_ValidItem() {
        // Given
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);

        // When & Then
        assertDoesNotThrow(() -> validator.validateItem(item, mockCart));
    }

    @Test
    public void testValidateItem_NullItem() {
        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateItem(null, mockCart));
    }

    @Test
    public void testValidateItem_NullCart() {
        // Given
        IItem item = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);

        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, null));
    }

    @Test
    public void testValidateCategory_VasItemDirectlyAdded() {
        // Given
        VasItem item = new VasItem(1, 3, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(20.0), 1);

        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, mockCart), "VAS items cannot be added directly to the cart.");
    }

    @Test
    public void testValidateCategory_ValidDigitalItem() {
        // Given
        IItem item = new DigitalItem(1, 2001, BigDecimal.valueOf(50.0), 2);

        // When & Then
        assertDoesNotThrow(() -> validator.validateItem(item, mockCart));
    }

    @Test
    public void testValidateCategory_InvalidDigitalItem() {
        // Given
        when(mockCart.getDigitalItemCount()).thenReturn(Config.MAX_DIGITAL_ITEM_QUANTITY);
        IItem item = new DigitalItem(Config.MAX_DIGITAL_ITEM_QUANTITY + 1, 2001, BigDecimal.valueOf(50.0), 1);

        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, mockCart), "Adding digital items exceeds the allowed limit.");
    }

    @Test
    public void testValidateVasItem_ValidVasItem() {
        // Given
        IItem parentItem = TestUtils.createDefaultItem(1, Config.VALID_CATEGORIES_FOR_VAS.iterator().next(), 2001, 100.0, 1);
        VasItem vasItem = new VasItem(1, 2, BigDecimal.valueOf(50.0), 1);
        when(mockCart.getItems()).thenReturn(Map.of(parentItem.getId(), parentItem));

        // When & Then
        assertDoesNotThrow(() -> validator.validateVasItem(vasItem, mockCart));
    }

    @Test
    public void testValidateVasItem_NullVasItem() {
        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateVasItem(null, mockCart));
    }

    @Test
    public void testValidateVasItem_ParentItemNotFound() {
        // Given
        VasItem vasItem = new VasItem(1, 2, BigDecimal.valueOf(50.0), 1);
        when(mockCart.getItems()).thenReturn(Map.of());

        // When & Then
        assertThrows(ItemNotFoundException.class, () -> validator.validateVasItem(vasItem, mockCart));
    }

    @Test
    public void testValidateVasItem_InvalidParentItem() {
        // Given
        IItem parentItem = TestUtils.createDigitalItem(1, 2001, 100.0, 1);
        VasItem vasItem = new VasItem(1, 2, BigDecimal.valueOf(50.0), 1);
        when(mockCart.getItems()).thenReturn(Map.of(parentItem.getId(), parentItem));

        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateVasItem(vasItem, mockCart));
    }

    @Test
    public void testValidateMaxTotalQuantity() {
        // Given
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);
        when(mockCart.getTotalItemCount()).thenReturn(Config.MAX_TOTAL_ITEMS - 1);

        // When & Then
        assertDoesNotThrow(() -> validator.validateItem(item, mockCart));
    }

    @Test
    public void testValidateTotalQuantityInvalid() {
        // Given
        IItem item = new DefaultItem(Config.MAX_TOTAL_ITEMS + 1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 4);
        when (mockCart.getTotalItemCount()).thenReturn(Config.MAX_TOTAL_ITEMS + 1);

        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, mockCart), "Adding items exceeds the total allowed limit.");
    }

    @Test
    public void testValidateMaxTotalAmount() {
        // Given
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, Config.MAX_TOTAL_AMOUNT, 1);

        // When & Then
        assertDoesNotThrow(() -> validator.validateItem(item, mockCart));
    }

    @Test
    public void testValidateItem_TotalAmountInvalid() {
        // Given
        BigDecimal itemPrice = BigDecimal.valueOf(10000.0);
        IItem item2 = new DefaultItem(2, Category.FURNITURE.getId(), 2001, itemPrice, 1);
        when(mockCart.getTotalPrice()).thenReturn(Config.MAX_TOTAL_AMOUNT);
        when(mockCart.calculateBestPromotionWithTempItem(item2)).thenReturn(new PromotionResult(BigDecimal.valueOf(5000.00), 1));

        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item2, mockCart), "Adding items should cause total amount to exceed the allowed limit.");
    }

    @Test
    public void testValidateItem_TotalAmountValidWithDiscount() {
        // Given
        BigDecimal itemPrice = BigDecimal.valueOf(10000.0);
        IItem item2 = new DefaultItem(2, Category.FURNITURE.getId(), 2001, itemPrice, 1);
        when(mockCart.getTotalPrice()).thenReturn(Config.MAX_TOTAL_AMOUNT);
        when(mockCart.calculateBestPromotionWithTempItem(item2)).thenReturn(new PromotionResult(BigDecimal.valueOf(15000.00), 1));

        // When & Then
        assertDoesNotThrow(() -> validator.validateItem(item2, mockCart), "Adding items causes total amount to exceed the allowed limit.");
    }

    @Test
    public void testValidateMaxUniqueItems() {
        // Given
        Map<Integer, IItem> items = new java.util.HashMap<>(Map.of());
        for (int i = 0; i < Config.MAX_UNIQUE_ITEMS; i++) {
            IItem item = new DefaultItem(i, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);
            items.put(i, item);
        }
        when(mockCart.getItems()).thenReturn(items);
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);

        // When & Then
        assertDoesNotThrow(() -> validator.validateItem(item, mockCart));
    }

    @Test
    public void testValidateItem_UniqueItemCountInvalid() {
        // Given
        IItem item = new DefaultItem(Config.MAX_UNIQUE_ITEMS + 1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);
        when(mockCart.getUniqueItemCount()).thenReturn(Config.MAX_UNIQUE_ITEMS);

        // When & Then
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, mockCart), "Adding this item causes unique item count to exceed the allowed limit.");
    }
}
