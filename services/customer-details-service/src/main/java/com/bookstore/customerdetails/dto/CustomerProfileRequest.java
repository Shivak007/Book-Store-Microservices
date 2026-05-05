package com.bookstore.customerdetails.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class CustomerProfileRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private String phone;
}