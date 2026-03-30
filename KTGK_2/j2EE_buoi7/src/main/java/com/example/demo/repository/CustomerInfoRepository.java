package com.example.demo.repository;

import com.example.demo.model.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Long> {
}
