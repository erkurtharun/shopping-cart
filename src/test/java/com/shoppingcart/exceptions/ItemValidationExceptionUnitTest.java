package com.shoppingcart.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemValidationExceptionUnitTest {

    @Test
    public void testExceptionMessage() {
        String message = "Item validation failed";
        ItemValidationException exception = assertThrows(ItemValidationException.class, () -> {
            throw new ItemValidationException(message);
        });
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testExceptionMessageWithCause() {
        String message = "Item validation failed";
        Throwable cause = new RuntimeException("Cause of the error");
        ItemValidationException exception = assertThrows(ItemValidationException.class, () -> {
            throw new ItemValidationException(message, cause);
        });
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
