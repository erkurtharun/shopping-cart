package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RemoveItemCommandHandlerUnitTest {

    private ICart cart;
    private RemoveItemCommandHandler handler;

    @BeforeEach
    public void setUp() {
        cart = mock(ICart.class);
        handler = new RemoveItemCommandHandler(cart);
    }

    @Test
    public void testHandleCommand_Success() {
        JSONObject command = new JSONObject()
                .put("payload", new JSONObject().put("itemId", 1));
        JSONObject response = handler.handleCommand(command);

        verify(cart).removeItem(1);
        assertTrue(response.getBoolean("result"));
        assertEquals("Item removed successfully", response.getString("message"));
    }
}
