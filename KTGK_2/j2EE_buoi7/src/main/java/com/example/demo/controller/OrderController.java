package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.CustomerInfo;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.CustomerInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;


@Controller
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerInfoRepository customerInfoRepository;

    @GetMapping("/order/detail/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        CustomerInfo customerInfo = customerInfoRepository.findAll().stream().filter(c -> c.getOrder().getId().equals(id)).findFirst().orElse(null);
        model.addAttribute("order", order);
        model.addAttribute("customerInfo", customerInfo);
        return "order/detail";
    }

    @GetMapping("/order/my-orders")
    public String myOrders(Model model, Authentication authentication) {
        // Lấy login_name user hiện tại
        String loginName = authentication.getName();
        java.util.List<CustomerInfo> infos = customerInfoRepository.findAll();
        java.util.List<Order> orders = new java.util.ArrayList<>();
        for (CustomerInfo info : infos) {
            if (info.getLoginName() != null && info.getLoginName().equalsIgnoreCase(loginName) && info.getOrder() != null) {
                orders.add(info.getOrder());
            }
        }
        model.addAttribute("orders", orders);
        return "order/my-orders";
    }
}
