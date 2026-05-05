package com.bookstore.customerdetails;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class CustomerDetailsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerDetailsServiceApplication.class, args);
    }
}