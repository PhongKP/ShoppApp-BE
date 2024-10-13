package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse extends BaseEntityResponse{

    private Long id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    @JsonProperty("categories_id")
    private Long categoryId;

    @JsonProperty("product_images")
    private List<ProductImageResponse> productImageList = new ArrayList<>();

    public static ProductResponse fromProduct (Product product){

        List<ProductImageResponse> images = product.getProductImageList().stream()
                .map(ProductResponse::convertProductImageDTO)
                .collect(Collectors.toList());

        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategories().getId())
                .productImageList(images)
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }

    private static ProductImageResponse convertProductImageDTO(ProductImage productImage){
        ProductImageResponse productImageResponse = new ProductImageResponse();
        productImageResponse.setId(productImage.getId());
        productImageResponse.setProductId(productImage.getProduct().getId());
        productImageResponse.setImageUrl(productImage.getImage_url());
        return productImageResponse;
    }

}
