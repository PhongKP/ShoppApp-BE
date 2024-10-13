package com.project.shopapp.services;

import com.project.shopapp.dto.ProductDTO;
import com.project.shopapp.dto.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.ProductImage;
import com.project.shopapp.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {

    Product createProduct (ProductDTO productDTO) throws DataNotFoundException;

    Product getProductById (Long id) throws DataNotFoundException;

    Page<ProductResponse> getAllProduct (String keyword, Long categoryId, PageRequest pageRequest);

    Product updateProduct (Long id, ProductDTO productDTO) throws DataNotFoundException;

    void deleteProduct(Long id) throws DataNotFoundException;

    ProductImage createFileProductImage (Long productId, ProductImageDTO productImageDTO) throws Exception;

    boolean existsByName (String name);

    List<Product> findProductsByListId(List<Long> productIds);

}
