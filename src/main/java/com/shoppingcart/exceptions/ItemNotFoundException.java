package com.shoppingcart.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemNotFoundException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(ItemNotFoundException.class);

    public ItemNotFoundException(String message) {
        super(message);
        logger.error("Item not found error: {}", message);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
        logger.error("Item not found error: {}", message, cause);
    }
}
