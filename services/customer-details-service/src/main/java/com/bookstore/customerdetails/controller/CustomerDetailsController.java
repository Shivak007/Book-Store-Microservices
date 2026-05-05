package com.bookstore.customerdetails.controller;
import com.bookstore.customerdetails.dto.*;
import com.bookstore.customerdetails.entity.*;
import com.bookstore.customerdetails.service.CustomerDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Details", description = "Customer profile and address APIs")
public class CustomerDetailsController {
    private final CustomerDetailsService service;
    @Operation(summary = "Get profile") @GetMapping("/details")
    public ResponseEntity<CustomerProfile> getProfile(@RequestHeader("userId") Long userId) {
        return ResponseEntity.ok(service.getProfile(userId)); }
    @Operation(summary = "Create profile") @PostMapping("/details")
    public ResponseEntity<CustomerProfile> createProfile(@RequestHeader("userId") Long userId, @Valid @RequestBody CustomerProfileRequest req) {
        return ResponseEntity.ok(service.createProfile(userId, req)); }
    @Operation(summary = "Update profile") @PutMapping("/details")
    public ResponseEntity<CustomerProfile> updateProfile(@RequestHeader("userId") Long userId, @Valid @RequestBody CustomerProfileRequest req) {
        return ResponseEntity.ok(service.updateProfile(userId, req)); }
    @Operation(summary = "Add address") @PostMapping("/addresses")
    public ResponseEntity<Address> addAddress(@RequestHeader("userId") Long userId, @Valid @RequestBody AddressRequest req) {
        return ResponseEntity.ok(service.addAddress(userId, req)); }
    @Operation(summary = "Get addresses") @GetMapping("/addresses")
    public ResponseEntity<List<Address>> getAddresses(@RequestHeader("userId") Long userId) {
        return ResponseEntity.ok(service.getAddresses(userId)); }
    @Operation(summary = "Delete address") @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        service.deleteAddress(id); return ResponseEntity.noContent().build(); }
    @Operation(summary = "Set default") @PutMapping("/addresses/{id}/default")
    public ResponseEntity<Address> setDefault(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        return ResponseEntity.ok(service.setDefaultAddress(id, userId)); }
}