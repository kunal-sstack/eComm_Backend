package com.projectX.backend.Service;

import com.projectX.backend.Entity.*;
import com.projectX.backend.Exceptions.APIException;
import com.projectX.backend.Exceptions.ResourceNotFoundException;
import com.projectX.backend.Payloads.OrderDTO;
import com.projectX.backend.Payloads.OrderItemDTO;
import com.projectX.backend.Payloads.OrderResponse;
import com.projectX.backend.Repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{
    
    @Autowired private UserRepository ur;
    @Autowired private CartRepository cr;
    @Autowired private OrderRepository or;
    @Autowired private PaymentRepository pr;
    @Autowired private OrderItemRepository oir;
    @Autowired private CartItemRepository cir;
    @Autowired private UserService us;
    @Autowired private CartService cs;
    @Autowired private ModelMapper mM;
    
    @Override
    public OrderDTO placeOrder(String emailId, Long cartId, String paymentMethod) {

        Cart cart = cr.findCartByEmailAndCartId(emailId,cartId);
        if(cart == null) throw new ResourceNotFoundException("Cart","cartId",cartId);

        Order order = new Order();

        order.setEmail(emailId);
        order.setOrderCost(cart.getTotalCartPrice());
        order.setOrderStatus("Order Accepted.");

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);

        pr.save(payment);

        Order savedOrder = or.save(order);
        List<CartItem> cartItems = cart.getCi();
        if(cartItems.isEmpty()) throw new APIException("no items in cart");
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem ci : cartItems){
            OrderItem oi = new OrderItem();
            oi.setProduct(ci.getProduct());
            oi.setQty(ci.getQty());
            oi.setDiscount(ci.getDiscount());
            oi.setOrderedProductPrice(ci.getCartItemPrice());
            oi.setOrder(savedOrder);

            orderItems.add(oi);
        }

        orderItems = oir.saveAll(orderItems);
        cart.getCi().forEach(item->{
            int qty = item.getQty();
            Product prd = item.getProduct();
            cs.deleteProductFromCart(cartId,prd.getProductId());
            prd.setQty(prd.getQty() - qty);
        });

        OrderDTO oDTO = mM.map(savedOrder, OrderDTO.class);
        orderItems.forEach(oi -> oDTO.getOrderItems().add(mM.map(oi, OrderItemDTO.class)));

        return oDTO;
    }

    @Override
    public List<OrderDTO> getOrdersByUser(String emailId) {

        List<Order> orders = or.findAllByEmail(emailId);
        List<OrderDTO> orderDTOS =orders.stream().map(order -> mM.map(order, OrderDTO.class)).collect(Collectors.toList());

        if(orderDTOS.isEmpty()) throw new APIException("No orders.");

        return orderDTOS;
    }

    @Override
    public OrderDTO getOrder(String emailId, Long orderId) {

        Order order = or.findOrderByEmailAndOrderId(emailId,orderId);
        if(order == null) throw new ResourceNotFoundException("Order","orderId",orderId);

        return mM.map(order, OrderDTO.class);
    }

    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable detailsOfPage = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Order> orderPage = or.findAll(detailsOfPage);
        List<Order> orders = orderPage.getContent();

        List<OrderDTO> orderDTOS = orders.stream().map(order -> mM.map(order, OrderDTO.class)).collect(Collectors.toList());
        if(orderDTOS.isEmpty()) throw new APIException("No orders");

        OrderResponse orr = new OrderResponse();
        orr.setContent(orderDTOS);
        orr.setPageNumber(orderPage.getNumber());
        orr.setPageSize(orderPage.getSize());
        orr.setTotalPages((long)orderPage.getTotalPages());
        orr.setLastPage(orderPage.isLast());

        return orr;
    }

    @Override
    public OrderDTO updateOrder(String emailId, Long orderId, String orderStatus) {

        Order order = or.findOrderByEmailAndOrderId(emailId,orderId);

        if(order == null) throw new ResourceNotFoundException("Order","orderId",orderId);

        order.setOrderStatus(orderStatus);

        return mM.map(order, OrderDTO.class);
    }
}
