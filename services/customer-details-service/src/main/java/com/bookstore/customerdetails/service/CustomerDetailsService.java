package com.bookstore.customerdetails.service;
import com.bookstore.customerdetails.dto.*;
import com.bookstore.customerdetails.entity.*;
import com.bookstore.customerdetails.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CustomerDetailsService {
    private final CustomerProfileRepository profileRepository;
    private final AddressRepository addressRepository;
    public CustomerProfile getProfile(Long userId) {
        return profileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
    }
    public CustomerProfile createProfile(Long userId, CustomerProfileRequest req) {
        CustomerProfile p = CustomerProfile.builder().userId(userId)
            .firstName(req.getFirstName()).lastName(req.getLastName()).phone(req.getPhone()).build();
        return profileRepository.save(p);
    }
    public CustomerProfile updateProfile(Long userId, CustomerProfileRequest req) {
        CustomerProfile p = getProfile(userId);
        p.setFirstName(req.getFirstName()); p.setLastName(req.getLastName()); p.setPhone(req.getPhone());
        return profileRepository.save(p);
    }
    public Address addAddress(Long userId, AddressRequest req) {
        Address a = Address.builder().userId(userId).street(req.getStreet()).city(req.getCity())
            .state(req.getState()).zipCode(req.getZipCode()).country(req.getCountry()).isDefault(req.isDefault()).build();
        return addressRepository.save(a);
    }
    public List<Address> getAddresses(Long userId) { return addressRepository.findByUserId(userId); }
    public void deleteAddress(Long id) { addressRepository.deleteById(id); }
    public Address setDefaultAddress(Long addressId, Long userId) {
        addressRepository.findByUserId(userId).forEach(a -> { a.setDefault(false); addressRepository.save(a); });
        Address a = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        a.setDefault(true);
        return addressRepository.save(a);
    }
}