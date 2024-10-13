package com.project.shopapp.controller;

import com.project.shopapp.dto.OrderDTO;
import com.project.shopapp.response.OrderListResponse;
import com.project.shopapp.response.OrderResponse;
import com.project.shopapp.services.ICartService;
import com.project.shopapp.services.IOrderService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;
    private final ICartService cartService;

    @PostMapping("")
    public ResponseEntity<?> createOrder (
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ){
        try{
            if (result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderResponse orderResponse = orderService.createOrder(orderDTO);
            cartService.deleteByUserIdAfterSaveOrder(orderResponse.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrdersByUserId (@Valid @PathVariable("user_id") Long userId){
        try {
            List<OrderResponse> orderResponseList = orderService.getAllOrdersByUserId(userId);
            return ResponseEntity.ok(orderResponseList);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/{user_id}")
    public ResponseEntity<?> getAllOrderByUserId(
            @PathVariable("user_id") Long userId,
            @RequestParam(defaultValue = "", name = "keyword") String keyword,
            @RequestParam(defaultValue = "PENDING", name = "status") String status,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) throws Exception {
        PageRequest pageRequest = PageRequest.of(
                page-1, limit,
                Sort.by("orderDate").descending()
        );

        Page<OrderResponse> orderPage = orderService.getOrdersByUserId(userId,keyword,status,pageRequest);
        int totalPage = orderPage.getTotalPages();
        List<OrderResponse> orderResponseList = orderPage.getContent();
        return ResponseEntity.ok().body(OrderListResponse.builder()
                .orderResponseList(orderResponseList)
                .totalPage(totalPage)
                .build()
        );
    }

    @GetMapping("/get-orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrder (
            @RequestParam(defaultValue = "", name="keyword") String keyword,
            @RequestParam("page") int page,
            @RequestParam(defaultValue = "10", name="limit") int limit
    ) throws Exception{
        PageRequest pageRequest = PageRequest.of(
                page-1, limit,
                Sort.by("orderDate").descending()
        );

        Page<OrderResponse> orders = orderService.getAllOrder(keyword,pageRequest);
        int totalPage = orders.getTotalPages();
        List<OrderResponse> orderResponseList = orders.getContent();
        return ResponseEntity.ok().body(OrderListResponse.builder()
                .orderResponseList(orderResponseList)
                .totalPage(totalPage)
                .build()
        );
    }


    @GetMapping("/{order_id}")
    public ResponseEntity<?> getOrderById (@Valid @PathVariable("order_id") Long orderId){
        try {
            OrderResponse orderResponse = orderService.getOrderById(orderId);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{order_id}")
    public ResponseEntity<?> updateOrderById (
            @Valid @PathVariable("order_id") Long orderId,
            @Valid @RequestBody OrderDTO newOrderDTO,
            BindingResult result
    ){
        try {
            if (result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderResponse orderResponse = orderService.updateOrder(orderId,newOrderDTO);
            return ResponseEntity.ok(orderResponse);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{order_id}")
    public ResponseEntity<?> deleteOrderById (@Valid @PathVariable("order_id") Long id){
        try {
            orderService.deleteOrderById(id);
            return ResponseEntity.ok("Xóa order với id = " + id + " Thành công");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
