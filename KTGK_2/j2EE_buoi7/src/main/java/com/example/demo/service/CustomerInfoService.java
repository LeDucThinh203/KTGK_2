package com.example.demo.service;

import com.example.demo.model.CustomerInfo;
import com.example.demo.repository.CustomerInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerInfoService {
    @Autowired
    private CustomerInfoRepository customerInfoRepository;

    public CustomerInfo save(CustomerInfo info) {
        return customerInfoRepository.save(info);
    }
}
