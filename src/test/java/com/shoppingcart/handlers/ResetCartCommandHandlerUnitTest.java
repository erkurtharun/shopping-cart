package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResetCartCommandHandlerUnitTest {

    private ICart cart;
    private ResetCartCommandHandler handler;

    @BeforeEach
    public void setUp() {
        cart = mock(ICart.class);
        handler = new ResetCartCommandHandler(cart);
    }

    @Test
    public void testHandleCommand_Success() {
        JSONObject command = new JSONObject().put("command", "resetCart");
        JSONObject response = handler.handleCommand(command);

        verify(cart).reset();
        assertTrue(response.getBoolean("result"));
        assertEquals("Cart has been reset successfully", response.getString("message"));
    }
}
