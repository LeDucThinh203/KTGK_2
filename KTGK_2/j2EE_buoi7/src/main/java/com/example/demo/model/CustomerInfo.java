package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customer_info")
public class CustomerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_name")
    private String loginName; // Lưu login_name của account
    private String phone;
    private String address;
    private String email;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
