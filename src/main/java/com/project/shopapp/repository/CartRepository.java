package com.project.shopapp.repository;

import com.project.shopapp.model.Cart;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("select c from Cart c where c.user.id = :userId")
    Optional<Cart> findCartByUserId(@Param("userId") Long userId);

    @Query("select ci.product from Cart c join CartItem ci where c.user.id = ?1")
    List<Product> getAllProductsByUserId(Long userId);

    @Query("select c.user from Cart c where c.user.id = ?1")
    Optional<User> findUserOfCart(Long userId);

    @Modifying
    @Query("delete from Cart c where c.id = :cartId")
    void deleteById (@Param("cartId") Long cartId);

}
