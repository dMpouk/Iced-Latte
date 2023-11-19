package com.zufar.icedlatte.cart.api;

import com.zufar.icedlatte.openapi.dto.DeleteItemsFromShoppingSessionRequest;
import com.zufar.icedlatte.openapi.dto.NewShoppingSessionItemDto;
import com.zufar.icedlatte.openapi.dto.ShoppingSessionDto;
import com.zufar.icedlatte.cart.exception.InvalidShoppingSessionIdException;
import com.zufar.icedlatte.cart.exception.ShoppingSessionItemNotFoundException;
import com.zufar.icedlatte.cart.exception.ShoppingSessionNotFoundException;
import com.zufar.icedlatte.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartManager implements CartApi {

    private final AddItemsToShoppingSessionHelper addItemsToShoppingSessionHelper;
    private final ProductQuantityItemUpdater productQuantityItemUpdater;
    private final ShoppingSessionProvider shoppingSessionProvider;
    private final ShoppingSessionItemsDeleter shoppingSessionItemsDeleter;

    @Override
    public ShoppingSessionDto getShoppingSessionByUserId(final UUID userId) throws ShoppingSessionNotFoundException {
        return shoppingSessionProvider.getByUserId(userId);
    }

    @Override
    public ShoppingSessionDto addItemsToShoppingSession(final Set<NewShoppingSessionItemDto> items) throws ShoppingSessionNotFoundException, ShoppingSessionItemNotFoundException, ProductNotFoundException {
        return addItemsToShoppingSessionHelper.add(items);
    }

    @Override
    public ShoppingSessionDto deleteItemsFromShoppingSession(final DeleteItemsFromShoppingSessionRequest deleteItemsFromShoppingSessionRequest) {
        return shoppingSessionItemsDeleter.delete(deleteItemsFromShoppingSessionRequest);
    }

    @Override
    public ShoppingSessionDto updateProductQuantityInShoppingSessionItem(final UUID shoppingSessionItemId,
                                                                          final int productQuantityChange) throws ShoppingSessionNotFoundException, ShoppingSessionItemNotFoundException, InvalidShoppingSessionIdException {
        return productQuantityItemUpdater.update(shoppingSessionItemId, productQuantityChange);
    }
}