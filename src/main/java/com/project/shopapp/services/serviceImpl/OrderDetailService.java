package com.project.shopapp.services.serviceImpl;

import com.project.shopapp.dto.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.Order;
import com.project.shopapp.model.OrderDetail;
import com.project.shopapp.model.Product;
import com.project.shopapp.repository.OrderDetailRepository;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.response.OrderDetailResponse;
import com.project.shopapp.response.OrderResponse;
import com.project.shopapp.services.IOrderDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDetailService implements IOrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderDetailResponse getOrderDetailById(Long id) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy order detail với id = " +
                        id));
        return OrderDetailResponse.fromOrderDetail(orderDetail);
    }

    @Override
    public List<OrderDetailResponse> getAllOrderDetail(Long orderId) throws Exception {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(()-> new DataNotFoundException("Không tìm thấy order id = " + orderId));
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(existingOrder.getId());
        return orderDetailList.stream().map(orderDetail ->
                OrderDetailResponse.fromOrderDetail(orderDetail))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailDTO newOrderDetailDTO) throws Exception {
        Order existingOrder = orderRepository.findById(newOrderDetailDTO.getOrderId())
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy order detail với order id = " +
                                newOrderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(newOrderDetailDTO.getProductId())
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy product với id = " +
                                newOrderDetailDTO.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                        .order(existingOrder)
                        .product(existingProduct)
                        .price(newOrderDetailDTO.getPrice())
                        .numberOfProduct(Math.toIntExact(newOrderDetailDTO.getNumberOfProduct()))
                        .totalPrice(newOrderDetailDTO.getTotalPrice())
                        .color(newOrderDetailDTO.getColor())
                        .build();
        orderDetailRepository.save(orderDetail);
        return modelMapper.map(orderDetail,OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO newOrderDetailDTO)
        throws Exception
    {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy order detail với id = " + id));
        Order existingOrder = orderRepository.findById(newOrderDetailDTO.getOrderId())
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy order với id = " +
                                newOrderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(newOrderDetailDTO.getProductId())
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy pruduct với id = " +
                                newOrderDetailDTO.getProductId()));
        existingOrderDetail.setProduct(existingProduct);
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setPrice(newOrderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProduct(Math.toIntExact(newOrderDetailDTO.getNumberOfProduct()));
        existingOrderDetail.setTotalPrice(newOrderDetailDTO.getTotalPrice());
        existingOrderDetail.setColor(newOrderDetailDTO.getColor());
        return OrderDetailResponse.fromOrderDetail(orderDetailRepository.save(existingOrderDetail));
    }

    @Override
    public void deleteOrderDetailById(Long id) throws Exception{
        Optional<OrderDetail> OptExistingOrderDetail = orderDetailRepository.findById(id);
        if (!OptExistingOrderDetail.isPresent())
            throw new DataNotFoundException("Không tìm thấy order detail với id = " + id);
        orderDetailRepository.delete(OptExistingOrderDetail.get());
    }
}
