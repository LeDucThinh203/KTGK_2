package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.Product;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Order createOrder(List<OrderDetail> orderDetails, long totalAmount) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);
        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
        }
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }
}
