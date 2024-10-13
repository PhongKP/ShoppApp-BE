package com.project.shopapp.controller;

import com.project.shopapp.dto.CartDTO;
import com.project.shopapp.model.Cart;
import com.project.shopapp.model.CartItem;
import com.project.shopapp.response.CartResponse;
import com.project.shopapp.services.ICartItemService;
import com.project.shopapp.services.ICartService;
import com.project.shopapp.utils.LocalizationUtils;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/carts")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;
    private final ICartItemService cartItemService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartByUserId(@PathVariable("id") Long userId){
        try {
            Cart cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(CartResponse.fromCart(cart,"Get Cart Successfully"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> saveToCart(
            @Valid @RequestBody CartDTO cartDTO,
            BindingResult result
    ){
        if (result.hasErrors()){
            List<String> errorList = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    CartResponse.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.CHECK_CART_FAILED,errorList))
                            .build());
        }
        try {
            Cart cart = cartService.saveToCart(cartDTO);
            return ResponseEntity.ok(CartResponse.fromCart(cart,"success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(CartResponse.fromCart(null, "failed"));
        }
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<?> updateCart(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody CartDTO cartDTO
    ){
        try{
            Cart cart = cartService.updateCart(userId, cartDTO);
            return ResponseEntity.ok(CartResponse.fromCart(cart,"success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable("user_id") Long userId,
            @RequestParam("product_id") Long productId
    ){
        try {
            CartResponse existingCart = ((ResponseEntity<CartResponse>) this.getCartByUserId(userId)).getBody();
            if (existingCart != null)
                cartItemService.deleteByCartIdAndProductId(existingCart.getId(),productId);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
