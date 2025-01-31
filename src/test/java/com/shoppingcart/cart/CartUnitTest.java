package com.shoppingcart.cart;

import com.shoppingcart.exceptions.ItemNotFoundException;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.IItem;
import com.shoppingcart.models.VasItem;
import com.shoppingcart.promotions.*;
import com.shoppingcart.testutil.TestUtils;
import com.shoppingcart.validation.CartItemValidator;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartUnitTest {

    private Cart cart;
    private CartItemValidator mockCartItemValidator;
    private PromotionService mockPromotionService;

    @BeforeEach
    void setUp() {
        mockPromotionService = mock(PromotionService.class);
        mockCartItemValidator = mock(CartItemValidator.class);
        PromotionResult mockPromotionResult = new PromotionResult(BigDecimal.valueOf(50), 1);
        when(mockPromotionService.calculateBestPromotion(any())).thenReturn(mockPromotionResult);
        cart = new Cart(mockPromotionService);
        cart.setCartItemValidator(mockCartItemValidator);
    }

    @Test
    void shouldAddNewItemToCart() {
        // GIVEN: A new item
        IItem item = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);

        // WHEN: The item is added to the cart
        cart.addItem(item);

        // THEN: The cart should contain the item with correct details
        assertEquals(1, cart.getUniqueItemCount());
        assertEquals(1, cart.getTotalItemCount());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(cart.getTotalPrice()));
        verify(mockCartItemValidator).validateItem(any(), eq(cart));
        verify(mockPromotionService).calculateBestPromotion(cart);
    }

    @Test
    void shouldIncreaseItemQuantity_WhenAddingExistingItem() {
        // GIVEN: An item is added to the cart
        IItem item1 = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);
        IItem item2 = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 2);
        cart.addItem(item1);

        // WHEN: The same item is added again
        cart.addItem(item2);

        // THEN: The item quantity should increase
        assertEquals(1, cart.getUniqueItemCount());
        assertEquals(3, cart.getTotalItemCount());
        assertEquals(0, BigDecimal.valueOf(300.0).compareTo(cart.getTotalPrice()));
        verify(mockCartItemValidator, times(2)).validateItem(any(), eq(cart));
        verify(mockPromotionService, times(2)).calculateBestPromotion(cart);
    }

    @Test
    void shouldThrowException_WhenExceedingMaxQuantity() {
        // GIVEN: An item with max quantity added
        IItem item1 = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 10);
        cart.addItem(item1);

        // WHEN / THEN: Adding another quantity should throw an exception
        IItem item2 = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);
        assertThrows(ItemValidationException.class, () -> cart.addItem(item2));
    }

    @Test
    void shouldAddVasItemSuccessfully() {
        // GIVEN: A parent item and a VAS item
        IItem parentItem = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);
        VasItem vasItem = TestUtils.createVasItem(1, 2, 50.0, 1);
        cart.addItem(parentItem);

        // WHEN: The VAS item is added
        cart.addVasItem(vasItem);

        // THEN: The cart should reflect the total price including the VAS item
        assertEquals(1, cart.getUniqueItemCount());
        assertEquals(1, cart.getTotalItemCount());
        assertEquals(0, BigDecimal.valueOf(150.0).compareTo(cart.getTotalPrice()));
        verify(mockCartItemValidator).validateVasItem(any(), eq(cart));
        verify(mockPromotionService, times(2)).calculateBestPromotion(cart);
    }

    @Test
    void shouldThrowException_WhenAddingInvalidVasItem() {
        // GIVEN: An invalid VAS item
        VasItem vasItem = TestUtils.createVasItem(1, 2, 50.0, 1);
        doThrow(new ItemValidationException("Item validation failed")).when(mockCartItemValidator).validateVasItem(any(), any());

        // WHEN / THEN: Adding the invalid VAS item should throw an exception
        assertThrows(ItemValidationException.class, () -> cart.addVasItem(vasItem));
    }

    @Test
    void shouldRemoveItemFromCart() {
        // GIVEN: An item is added to the cart
        IItem item = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);
        cart.addItem(item);

        // WHEN: The item is removed
        cart.removeItem(1);

        // THEN: The cart should be empty
        assertEquals(0, cart.getUniqueItemCount());
        assertEquals(0, cart.getTotalItemCount());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalPrice()));
    }

    @Test
    void shouldThrowException_WhenRemovingNonExistentItem() {
        // WHEN / THEN: Removing an item that does not exist should throw an exception
        assertThrows(ItemNotFoundException.class, () -> cart.removeItem(1));
    }

    @Test
    void shouldResetCartSuccessfully() {
        // GIVEN: An item is added to the cart
        IItem item = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);
        cart.addItem(item);

        // WHEN: The cart is reset
        cart.reset();

        // THEN: The cart should be empty
        assertEquals(0, cart.getItems().size());
        assertEquals(0, cart.getTotalItemCount());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalAmount()));
    }

    @Test
    void shouldDisplayCartDetails() {
        // GIVEN: An item is added to the cart
        IItem item = TestUtils.createDefaultItem(1, 1001, 2001, 200.0, 1);
        when(mockPromotionService.calculateBestPromotion(cart)).thenReturn(new PromotionResult(BigDecimal.valueOf(50), 1));
        cart.addItem(item);

        // WHEN: The cart is displayed
        JSONObject response = cart.display();

        // THEN: The response should contain correct cart details
        assertTrue(response.getBoolean("result"));
        JSONObject message = response.getJSONObject("message");
        assertEquals(1, message.getJSONArray("items").length());
        assertEquals(0, BigDecimal.valueOf(150.0).compareTo(message.getBigDecimal("totalAmount")));
    }
}