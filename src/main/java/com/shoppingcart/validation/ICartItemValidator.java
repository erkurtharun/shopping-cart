package com.shoppingcart.validation;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.models.IItem;
import com.shoppingcart.models.VasItem;

public interface ICartItemValidator {
    void validateItem(IItem item, ICart cart);

    void validateVasItem(VasItem vasItem, ICart cart);
}
