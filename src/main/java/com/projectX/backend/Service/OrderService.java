package com.projectX.backend.Service;

import com.projectX.backend.Payloads.OrderDTO;
import com.projectX.backend.Payloads.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderDTO placeOrder(String emailId, Long cartId, String paymentMethod);
    List<OrderDTO> getOrdersByUser(String emailId);
    OrderDTO getOrder(String emailId, Long orderId);
    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    OrderDTO updateOrder(String emailId, Long orderId, String orderStatus);
}
