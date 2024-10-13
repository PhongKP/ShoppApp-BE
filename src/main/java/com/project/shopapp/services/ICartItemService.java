package com.project.shopapp.services;

public interface ICartItemService {

    void deleteByCartIdAndProductId(Long cartId, Long productId) throws Exception;

}
