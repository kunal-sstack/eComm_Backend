package com.projectX.backend.Controller;

import com.projectX.backend.Configuration.AppConstants;
import com.projectX.backend.Payloads.OrderDTO;
import com.projectX.backend.Payloads.OrderResponse;
import com.projectX.backend.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    public OrderService orderService;

    @PostMapping("/public/users/{emailId}/carts/{cartId}/payments/{paymentMethod}/order")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String emailId, @PathVariable Long cartId, @PathVariable String paymentMethod) {
        OrderDTO order = orderService.placeOrder(emailId, cartId, paymentMethod);

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.P_N, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.P_S, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.S_BY_PID, required = false) String sortBy,
            @RequestParam(name = "SortOrder", defaultValue = "ASC", required = false) String sortOrder) {

        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.FOUND);
    }

    @GetMapping("public/users/{emailId}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String emailId) {
        List<OrderDTO> orders = orderService.getOrdersByUser(emailId);

        return new ResponseEntity<List<OrderDTO>>(orders, HttpStatus.FOUND);
    }

    @GetMapping("public/users/{emailId}/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderByUser(@PathVariable String emailId, @PathVariable Long orderId) {
        OrderDTO order = orderService.getOrder(emailId, orderId);

        return new ResponseEntity<OrderDTO>(order, HttpStatus.FOUND);
    }

    @PutMapping("admin/users/{emailId}/orders/{orderId}/orderStatus/{orderStatus}")
    public ResponseEntity<OrderDTO> updateOrderByUser(@PathVariable String emailId, @PathVariable Long orderId, @PathVariable String orderStatus) {
        OrderDTO order = orderService.updateOrder(emailId, orderId, orderStatus);

        return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
    }

}
