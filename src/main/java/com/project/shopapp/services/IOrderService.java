package com.project.shopapp.services;

import com.project.shopapp.dto.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {

    OrderResponse createOrder(OrderDTO orderDTO) throws Exception;

    OrderResponse getOrderById(Long id) throws Exception;

    OrderResponse updateOrder (Long id, OrderDTO orderDTO) throws Exception;

    void deleteOrderById (Long id) throws Exception;

    List<OrderResponse> getAllOrdersByUserId(Long userId) throws Exception;

    Page<OrderResponse> getOrdersByUserId(Long userId, String keyword, String status, PageRequest pageRequest) throws Exception;

    Page<OrderResponse> getAllOrder(String keyword, PageRequest pageRequest);
}
