package com.project.shopapp.repository;

import com.project.shopapp.model.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Modifying
    @Query("delete from CartItem ci where ci.cart.id = :cartId and ci.product.id = :productId")
    void deleteByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    @Modifying
    @Query("delete from CartItem ci where ci.cart.id = :cartId")
    void deleteCartItemByCartId(@Param("cartId") Long cartId);

}
