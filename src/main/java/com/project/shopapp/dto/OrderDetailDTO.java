package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.model.OrderDetail;
import com.project.shopapp.response.OrderDetailResponse;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {

    @Min(value = 1, message = "Order ID must be greater than 0")
    @JsonProperty("order_id")
    private Long orderId;

    @Min(value = 1, message = "Product ID must be greater than 0")
    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 0, message = "Đơn giá phải lớn hơn hoặc bằng 0")
    private float price;

    @Min(value = 1, message = "Số lượng sản phẩm phải có ít nhất là 1")
    @JsonProperty("number_of_product")
    private Long numberOfProduct;

    @Min(value = 0, message = "Tổng số tiền phải lớn hơn hoặc bằng 0")
    @JsonProperty("total_price")
    private float totalPrice;

    private String color;
}
