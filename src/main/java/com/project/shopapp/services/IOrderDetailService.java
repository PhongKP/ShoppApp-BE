package com.project.shopapp.services;

import com.project.shopapp.dto.OrderDetailDTO;
import com.project.shopapp.response.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {

    OrderDetailResponse getOrderDetailById (Long Id) throws Exception;

    List<OrderDetailResponse> getAllOrderDetail (Long orderId) throws Exception;

    OrderDetailResponse createOrderDetail (OrderDetailDTO newOrderDetailDTO) throws Exception;

    OrderDetailResponse updateOrderDetail (Long id, OrderDetailDTO newOrderDetailDTO)
            throws Exception;

    void deleteOrderDetailById (Long id) throws Exception;

}
