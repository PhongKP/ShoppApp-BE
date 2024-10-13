package com.project.shopapp.controller;

import com.project.shopapp.dto.OrderDetailDTO;
import com.project.shopapp.response.OrderDetailResponse;
import com.project.shopapp.services.IOrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/order_detail")
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService orderDetailService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail (
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result){
        try {
            if (result.hasErrors()){
                List<String>  errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderDetailResponse OrderDetailResponse = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailById (@Valid @PathVariable Long id){
        try {
            OrderDetailResponse orderDetailResponse = orderDetailService.getOrderDetailById(id);
            return ResponseEntity.ok(orderDetailResponse);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/order/{order_id}")
    @CrossOrigin("http://localhost:4200")
    public ResponseEntity<?> getOrderDetailsByOrderId (@Valid @PathVariable("order_id") Long orderId){
        try {
            List<OrderDetailResponse> orderDetailResponseList =
                    orderDetailService.getAllOrderDetail(orderId);
            return ResponseEntity.ok(orderDetailResponseList);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetailById (
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDetailDTO newOrderDetailDTO
    ){
        try{
            OrderDetailResponse orderDetailResponse =
                    orderDetailService.updateOrderDetail(id, newOrderDetailDTO);
            return ResponseEntity.ok(orderDetailResponse);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetailById(@Valid @PathVariable Long id){
        try {
            orderDetailService.deleteOrderDetailById(id);
            return ResponseEntity.ok("Xóa thành công order detail với id = " + id);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
