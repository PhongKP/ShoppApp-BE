package com.project.shopapp.services;

import com.project.shopapp.dto.CartDTO;
import com.project.shopapp.model.Cart;
import org.springframework.stereotype.Service;

public interface ICartService {

    Cart saveToCart(CartDTO cartDTO);

    Cart updateCart(Long userId, CartDTO cartDTO);

    Cart getCartByUserId (Long userId) throws Exception;

    void deleteByUserIdAfterSaveOrder(Long userId) throws Exception;
}
