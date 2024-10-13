package com.project.shopapp.services.serviceImpl;

import com.project.shopapp.dto.CartItemsDTO;
import com.project.shopapp.dto.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.*;
import com.project.shopapp.repository.*;
import com.project.shopapp.response.OrderResponse;
import com.project.shopapp.services.IOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws Exception{
        // Check user ID exists hay không ?
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find user with id = " + orderDTO.getUserId()));
        // Convert DTO => Order Entity (Use ModelMapper)
        // Tùy chỉnh mapping theo ý muốn
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Thực hiện mapping
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);

        // Kiểm tra shipping Date >= today
        LocalDate shippingDate = orderDTO.getShippingDate() == null ?
                LocalDate.now() :
                orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shipping Date must be at least today");
        }

        // Tạo mã vận đơn
        String trackingNumber = generateTrackingNumber(32);
        order.setTrackingNumber(trackingNumber);

        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);

        //Tạo list orderDetail từ cartItemsDTO
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartItemsDTO cartItemsDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long productId = cartItemsDTO.getProductId();
            int quantity = Math.toIntExact(cartItemsDTO.getQuantity());

            Product existsProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm trong kho"));

            // Kiểm tra số lượng tồn kho (Nếu có)
            // Set data cho orderDetail
            orderDetail.setProduct(existsProduct);
            orderDetail.setNumberOfProduct(quantity);
            orderDetail.setPrice(existsProduct.getPrice());

            orderDetailList.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetailList);

        return modelMapper.map(order,OrderResponse.class);
    }


    private static String generateTrackingNumber(int length) {
        Random random = new Random();
        StringBuilder trackingNumber = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            trackingNumber.append(CHARACTERS.charAt(randomIndex));
        }
        return trackingNumber.toString();
    }

    @Override
    public OrderResponse getOrderById(Long id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy order với id = " + id));
        modelMapper.typeMap(Order.class, OrderResponse.class);
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws Exception {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy order cần sửa với id = " + id));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy user của các order với user id = " + id));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setUser(existingUser);
        orderRepository.save(existingOrder);
        return modelMapper.map(existingOrder,OrderResponse.class);
    }

    @Override
    public void deleteOrderById(Long id) throws Exception{
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent())
            throw new DataNotFoundException("Không tìm thấy order cần xóa với id = " + id);
        Order order = optionalOrder.get();
        order.setActive(false);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> getAllOrdersByUserId(Long userId) throws Exception{
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy các orders của user với id = " + userId));
        List<Order> orderList = orderRepository.findByUserId(existingUser.getId());
        List<OrderResponse> orderResponseList =
                orderList.stream()
                .map(order -> modelMapper.map(order,OrderResponse.class))
                .collect(Collectors.toList());
        return orderResponseList;
    }

    @Override
    public Page<OrderResponse> getOrdersByUserId(Long userId, String keyword, String status, PageRequest pageRequest) throws Exception {
        OrderStatus orderStatus = OrderStatus.fromString(status);
        return orderRepository.searchOrders(userId,keyword,orderStatus,pageRequest)
                .map(record -> {
                    Object[] fields = (Object[]) record;
                    Order order = (Order) fields[0];
                    Long productId = (Long) fields[1];
                    OrderResponse orderResponse = modelMapper.map(order,OrderResponse.class);
                    orderResponse.setProductId(productId);
                    return orderResponse;
                });
    }

    @Override
    public Page<OrderResponse> getAllOrder(String keyword, PageRequest pageRequest){
        return orderRepository.findByKeywordAdmin(keyword,pageRequest)
                .map(order -> modelMapper.map(order, OrderResponse.class));
    }
}
