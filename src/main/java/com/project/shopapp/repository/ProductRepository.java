package com.project.shopapp.repository;

import com.project.shopapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable); // Phân trang

    @Query(value="select * from products p " +
            "where (:categoryId IS NULL OR :categoryId = 0 or p.categories_id = :categoryId) "+
            "AND (:keyword is null or :keyword = '' or p.product_name like CONCAT('%', :keyword, '%') or p.description like CONCAT('%', :keyword, '%'))",
            nativeQuery = true
    )
    Page<Product> searchProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

    @Query("select p from Product p where p.id in :productIds")
    List<Product> findProductsByListId(@Param("productIds") List<Long> productIds);
}
