package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.model.CustomerInfo;
import com.example.demo.service.CustomerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerInfoService customerInfoService;

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            Product product = productService.getProductById(productId);
            cart.add(new CartItem(product, quantity));
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        long total = 0;
        for (CartItem item : cart) {
            total += item.getTotalPrice();
        }
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart/view";
    }

    @GetMapping("/checkout")
    public String showCheckoutForm(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart/view";
        }
        model.addAttribute("customerInfo", new CustomerInfo());
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(HttpSession session, Model model,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("address") String address,
                                 @RequestParam("email") String email,
                                 org.springframework.security.core.Authentication authentication) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart/view";
        }
        long total = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem item : cart) {
            OrderDetail detail = new OrderDetail();
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetails.add(detail);
            total += item.getTotalPrice();
        }
        // Tạo order
        Order order = orderService.createOrder(orderDetails, total);
        // Lưu thông tin khách hàng
        CustomerInfo info = new CustomerInfo();
        info.setLoginName(authentication.getName());
        info.setPhone(phone);
        info.setAddress(address);
        info.setEmail(email);
        info.setOrder(order);
        customerInfoService.save(info);
        session.removeAttribute("cart");
        return "redirect:/order/detail/" + order.getId();
    }

}
