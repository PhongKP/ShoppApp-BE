package com.project.shopapp.services.serviceImpl;

import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.CartItem;
import com.project.shopapp.repository.CartItemRepository;
import com.project.shopapp.services.ICartItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    public void deleteByCartIdAndProductId(Long cartId, Long productId) throws Exception {
        try {
            cartItemRepository.deleteByCartIdAndProductId(cartId,productId);
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

}
