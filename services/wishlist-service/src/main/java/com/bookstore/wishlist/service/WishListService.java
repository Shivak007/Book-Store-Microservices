package com.bookstore.wishlist.service;

import com.bookstore.wishlist.entity.WishList;
import com.bookstore.wishlist.entity.WishListItem;
import com.bookstore.wishlist.repository.WishListRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;

    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    public WishList getWishList() {
        return wishListRepository.findByUserId(getCurrentUserId()).orElseGet(() -> {
            WishList wl = new WishList();
            wl.setUserId(getCurrentUserId());
            return wishListRepository.save(wl);
        });
    }

    @Transactional
    public WishList addProduct(Long productId) {
        WishList wishList = getWishList();
        boolean exists = wishList.getItems().stream().anyMatch(i -> i.getProductId().equals(productId));
        if (!exists) {
            WishListItem item = new WishListItem();
            item.setProductId(productId);
            item.setWishList(wishList);
            wishList.getItems().add(item);
        }
        return wishListRepository.save(wishList);
    }

    @Transactional
    public WishList removeProduct(Long productId) {
        WishList wishList = getWishList();
        wishList.getItems().removeIf(i -> i.getProductId().equals(productId));
        return wishListRepository.save(wishList);
    }

    @Transactional
    public void clear() {
        WishList wishList = getWishList();
        wishList.getItems().clear();
        wishListRepository.save(wishList);
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
