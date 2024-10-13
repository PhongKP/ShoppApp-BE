package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @JsonProperty("user_id")
    @Min(value = 1, message = "User ID must be > 0")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    private String email;

    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Size(min = 10, max = 10, message = "Số điện thoại phải từ 10 số")
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String note;

    @NotNull(message = "Tổng tiền là bắt buộc")
    @Min(value = 0, message = "Số tiền phải lớn hơn hoặc bằng 0")
    @JsonProperty("total_amount")
    private float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @NotEmpty(message = "Địa chỉ nhận hàng là bắt buộc")
    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("cart_items")
    private List<CartItemsDTO> cartItems;
}
