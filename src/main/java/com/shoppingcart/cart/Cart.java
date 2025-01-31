package com.shoppingcart.cart;

import com.shoppingcart.config.Config;
import com.shoppingcart.exceptions.ErrorMessages;
import com.shoppingcart.exceptions.ItemNotFoundException;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.models.DigitalItem;
import com.shoppingcart.models.IItem;
import com.shoppingcart.models.VasItem;
import com.shoppingcart.promotions.PromotionResult;
import com.shoppingcart.promotions.PromotionService;
import com.shoppingcart.validation.CartItemValidator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

public class Cart implements ICart {

    private final Map<Integer, IItem> items;
    private final PromotionService promotionService;
    private BigDecimal totalAmount;
    private BigDecimal totalPrice;
    private BigDecimal nonVasTotalPrice;
    private BigDecimal totalDiscount;
    private int appliedPromotionId;
    private int uniqueItemCount;
    private int digitalItemCount;
    private int totalItemCount;
    private CartItemValidator cartValidator;

    public Cart(PromotionService promotionService) {
        this.items = new LinkedHashMap<>();
        this.promotionService = promotionService;
        this.totalDiscount = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
        this.nonVasTotalPrice = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.uniqueItemCount = 0;
        this.totalItemCount = 0;
        this.cartValidator = new CartItemValidator();
        this.appliedPromotionId = 0;
    }

    @Override
    public void addItem(IItem item) {
        cartValidator.validateItem(item, this);

        if (items.containsKey(item.getId())) {
            updateExistingItem(item);
        } else {
            addNewItem(item);
        }
        updateCounts(true, item);
        updateTotalPrice(true, item);
        applyPromotions();
    }

    @Override
    public void addVasItem(VasItem item) {
        cartValidator.validateVasItem(item, this);
        DefaultItem parentItem = (DefaultItem) items.get(item.getItemId());
        parentItem.addVasItem(item);
        updateTotalPrice(true, item);
        applyPromotions();
    }

    @Override
    public void removeItem(int itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(ErrorMessages.ITEM_NOT_FOUND.format(itemId));
        }
        IItem item = items.get(itemId);
        updateTotalPrice(false, item);
        updateCounts(false, item);
        items.remove(itemId);
        applyPromotions();
    }

    @Override
    public void reset() {
        items.clear();
        totalDiscount = BigDecimal.ZERO;
        totalPrice = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        nonVasTotalPrice = BigDecimal.ZERO;
        uniqueItemCount = 0;
        totalItemCount = 0;
        digitalItemCount = 0;
        appliedPromotionId = 0;
    }

    @Override
    public JSONObject display() {
        JSONObject message = new JSONObject();
        JSONArray itemsArray = new JSONArray();

        items.values().forEach(item -> {
            JSONObject itemJson = new JSONObject();
            itemJson.put("itemId", item.getId());
            itemJson.put("categoryId", item.getCategoryId());
            itemJson.put("sellerId", item.getSellerId());
            itemJson.put("price", item.getPrice());
            itemJson.put("quantity", item.getQuantity());

            if (item instanceof DefaultItem defaultItem) {
                itemJson.put("vasItems", createVasItemsArray(defaultItem.getVasItems()));
            } else {
                itemJson.put("vasItems", new JSONArray());
            }

            itemsArray.put(itemJson);
        });

        message.put("items", itemsArray);
        message.put("totalAmount", totalAmount);
        message.put("appliedPromotionId", getTotalDiscount().compareTo(BigDecimal.ZERO) > 0 ? appliedPromotionId : JSONObject.NULL);
        message.put("totalDiscount", totalDiscount);

        JSONObject response = new JSONObject();
        response.put("result", true);
        response.put("message", message);

        return response;
    }

    @Override
    public void applyPromotions() {
        PromotionResult promotionResult = promotionService.calculateBestPromotion(this);
        totalDiscount = promotionResult.discount();
        appliedPromotionId = promotionResult.promotionId();
        totalAmount = totalPrice.subtract(totalDiscount);
    }

    @Override
    public PromotionResult calculateBestPromotionWithTempItem(IItem item) {
        return promotionService.calculateBestPromotionWithNewItem(this, item);
    }

    @Override
    public Map<Integer, IItem> getItems() {
        return items;
    }

    @Override
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @Override
    public BigDecimal getNonVasTotalPrice() {
        return nonVasTotalPrice;
    }

    @Override
    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    @Override
    public int getDigitalItemCount() {
        return digitalItemCount;
    }

    @Override
    public int getAppliedPromotionId() {
        return appliedPromotionId;
    }

    @Override
    public int getUniqueItemCount() {
        return uniqueItemCount;
    }

    @Override
    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setCartItemValidator(CartItemValidator cartValidator) {
        this.cartValidator = cartValidator;
    }

    private void updateExistingItem(IItem item) {
        IItem existingItem = items.get(item.getId());
        int newQuantity = existingItem.getQuantity() + item.getQuantity();

        if (newQuantity > Config.MAX_QUANTITY_PER_ITEM) {
            throw new ItemValidationException(ErrorMessages.ITEM_VALIDATION_FAILED.format(item.getQuantity(),
                    item.getId(), existingItem.getQuantity(), item.getQuantity(), Config.MAX_QUANTITY_PER_ITEM));
        }
        existingItem.setQuantity(newQuantity);
    }

    private void addNewItem(IItem item) {
        items.put(item.getId(), item);
        uniqueItemCount++;
    }

    private void updateCounts(boolean isAdd, IItem item) {
        int multiplier = isAdd ? 1 : -1;
        if (item instanceof DigitalItem) {
            digitalItemCount += item.getQuantity() * multiplier;
        }
        totalItemCount += item.getQuantity() * multiplier;
        if (!isAdd) {
            uniqueItemCount--;
        }
    }

    private void updateTotalPrice(boolean isAdd, IItem item) {
        BigDecimal multiplier = isAdd ? BigDecimal.ONE : BigDecimal.valueOf(-1);

        if (!isAdd && item instanceof DefaultItem defaultItem) {
            defaultItem.getVasItems().forEach(vasItem -> updateTotalPrice(false, vasItem));
        }

        BigDecimal priceUpdate = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).multiply(multiplier);
        totalPrice = totalPrice.add(priceUpdate);
        if (!(item instanceof VasItem)) {
            nonVasTotalPrice = nonVasTotalPrice.add(priceUpdate);
        }
    }

    private JSONArray createVasItemsArray(List<VasItem> vasItems) {
        JSONArray vasItemsArray = new JSONArray();
        for (VasItem vasItem : vasItems) {
            JSONObject vasItemJson = new JSONObject();
            vasItemJson.put("vasItemId", vasItem.getId());
            vasItemJson.put("vasCategoryId", vasItem.getCategoryId());
            vasItemJson.put("vasSellerId", vasItem.getSellerId());
            vasItemJson.put("price", vasItem.getPrice());
            vasItemJson.put("quantity", vasItem.getQuantity());
            vasItemsArray.put(vasItemJson);
        }
        return vasItemsArray;
    }

}
