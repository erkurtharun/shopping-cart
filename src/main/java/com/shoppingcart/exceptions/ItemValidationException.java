package com.shoppingcart.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemValidationException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(ItemValidationException.class);

    public ItemValidationException(String message) {
        super(message);
        logger.error("Item validation error: {}", message);
    }

    public ItemValidationException(String message, Throwable cause) {
        super(message, cause);
        logger.error("Item validation error: {}", message, cause);
    }
}