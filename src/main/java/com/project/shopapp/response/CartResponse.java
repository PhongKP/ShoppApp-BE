package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("cart_id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("cart_item")
    private List<CartItemResponse> cartItems;

    public static CartResponse fromCart(Cart cart, String message) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .cartItems(cart.getCartItemList().stream()
                        .map(CartItemResponse::fromCartItem)
                        .collect(Collectors.toList()))
                .message(message)
                .build();
    }

}
