package com.shoppingcart.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemNotFoundExceptionUnitTest {

    @Test
    public void testExceptionMessage() {
        String message = "Item not found";
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () -> {
            throw new ItemNotFoundException(message);
        });
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testExceptionMessageWithCause() {
        String message = "Item not found";
        Throwable cause = new RuntimeException("Cause of the error");
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () -> {
            throw new ItemNotFoundException(message, cause);
        });
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
