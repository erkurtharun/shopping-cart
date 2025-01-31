package com.shoppingcart.config;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class Config {

    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    public static final int MAX_UNIQUE_ITEMS = getIntProperty("max.unique.items", 10);
    public static final int MAX_TOTAL_ITEMS = getIntProperty("max.total.items", 30);
    public static final BigDecimal MAX_TOTAL_AMOUNT = getBigDecimalProperty("max.total.amount", new BigDecimal("500000"));

    public static final int MAX_DIGITAL_ITEM_QUANTITY = getIntProperty("max.digital.item.quantity", 5);
    public static final int VAS_ITEM_SELLER_ID = getIntProperty("vas.item.seller.id", 5003);
    public static final int MAX_VAS_ITEMS_PER_DEFAULT_ITEM = getIntProperty("max.vas.items.per.default.item", 3);

    public static final int MAX_QUANTITY_PER_ITEM = getIntProperty("max.quantity.per.item", 10);

    public static final BigDecimal SAME_SELLER_PROMOTION_RATE = getBigDecimalProperty("same.seller.promotion.rate", new BigDecimal("0.10"));
    public static final Map<Integer, BigDecimal> DISCOUNT_CATEGORIES = getDiscountCategories();
    public static final Set<Integer> VALID_CATEGORIES_FOR_VAS = getValidCategoriesForVas();

    // Static Methods
    private static void loadProperties() {
        try {
            properties.load(Config.class.getClassLoader().getResourceAsStream("cart-config.properties"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load configuration properties: " + e.getMessage());
        }
    }

    // For testing purposes, allowing properties to be injected
    public static void setProperties(Properties props) {
        properties = props;
    }

    static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    static BigDecimal getBigDecimalProperty(String key, BigDecimal defaultValue) {
        try {
            return new BigDecimal(properties.getProperty(key, defaultValue.toString()));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static Map<Integer, BigDecimal> getDiscountCategories() {
        return Arrays.stream(properties.getProperty("discount.categories", "").split(","))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(
                        s -> Integer.parseInt(s[0]),
                        s -> new BigDecimal(s[1])
                ));
    }

    private static Set<Integer> getValidCategoriesForVas() {
        return Arrays.stream(properties.getProperty("valid.categories.for.vas", "").split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    public static void loadProperties(Properties props) throws IOException {
        properties = props;
        loadProperties();
    }
}
