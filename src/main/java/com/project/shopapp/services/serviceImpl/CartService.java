package com.project.shopapp.services.serviceImpl;

import com.project.shopapp.dto.CartDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.Cart;
import com.project.shopapp.model.CartItem;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.User;
import com.project.shopapp.repository.CartItemRepository;
import com.project.shopapp.repository.CartRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.services.ICartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public Cart saveToCart(CartDTO cartDTO) {
        try {
            User existingUser = userRepository.findById(cartDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("User này không tồn tại"));
            Product existingProduct = productRepository.findById(cartDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product này không tồn tại"));

            // Tìm hoặc tạo giỏ hàng mới cho người dùng
            Cart cart = cartRepository.findCartByUserId(existingUser.getId())
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(existingUser);
                        newCart.setCartItemList(new ArrayList<>());
                        return newCart;
                    });

            // Tìm CartItem hoặc tạo mới nếu chưa có
            CartItem cartItem = cart.getCartItemList().stream()
                    .filter(item -> item.getProduct().getId().equals(cartDTO.getProductId()))
                    .findFirst()
                    .orElseGet(() -> {
                        CartItem newItem = new CartItem();
                        newItem.setCart(cart);
                        newItem.setProduct(existingProduct);
                        cart.getCartItemList().add(newItem);
                        return newItem;
                    });

            cartItem.setQuantity(cartDTO.getQuantity());
            cartItemRepository.save(cartItem);
            return cartRepository.save(cart);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Cart updateCart(Long userId, CartDTO cartDTO) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy user này"));
            Product existingProduct = productRepository.findById(cartDTO.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Không tồn tại product này"));

            Cart cart = cartRepository.findCartByUserId(existingUser.getId())
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin giỏ hàng này"));

            CartItem cartItem = cart.getCartItemList().stream()
                    .filter(item -> item.getProduct().getId().equals(existingProduct.getId()))
                    .findFirst()
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

            cartItem.setQuantity(cartItem.getQuantity() + cartDTO.getQuantity());
            cartItemRepository.save(cartItem);
            return cartRepository.save(cart);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Cart getCartByUserId(Long userId) throws Exception {
        return cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy cart với user id = "+ userId));
    }

    @Override
    public void deleteByUserIdAfterSaveOrder(Long userId) throws Exception {
        try {
            Optional<Cart> cartOpt = cartRepository.findCartByUserId(userId);
            if (cartOpt.isPresent()){
                Cart existingCart = cartOpt.get();
                cartItemRepository.deleteCartItemByCartId(existingCart.getId());
                cartRepository.deleteById(existingCart.getId());
            }
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

}
