package com.project.shopapp.repository;

import com.project.shopapp.model.Order;
import com.project.shopapp.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findByUserId(Long userId);

    @Query("SELECT o, od.product.id FROM Order o " +
            "JOIN OrderDetail od on od.order.id = o.id " +
            "JOIN Product p on od.product.id = p.id " +
            "WHERE o.user.id = :userId " +
            "AND (:keyword IS NULL OR :keyword = '' or LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR o.status = :status)")
    Page<Object[]> searchOrders(@Param("userId") Long userId, @Param("keyword") String keyword,
                             @Param("status") OrderStatus status , Pageable pageable);


    @Query("select o from Order o where :keyword is null or :keyword = '' " +
            "or o.fullName like %:keyword% " +
            "or o.shippingAddress like %:keyword% " +
            "or o.email like %:keyword% " +
            "or o.trackingNumber like %:keyword% " +
            "or o.phoneNumber like %:keyword%"
    )
    Page<Order> findByKeywordAdmin (@Param("keyword") String keyword, Pageable pageable);

}
