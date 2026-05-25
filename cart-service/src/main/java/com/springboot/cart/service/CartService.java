package com.springboot.cart.service;

import com.springboot.cart.entity.Cart;
import com.springboot.cart.entity.CartItem;
import com.springboot.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(userId);
                    return cartRepository.save(newCart);
                });
    }
    
    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        Cart cart = getOrCreateCart(userId);
        
        // Get product details from Product Service
        String productServiceUrl = "http://product-service/api/products/" + productId;
        try {
            ProductDTO product = restTemplate.getForObject(productServiceUrl, ProductDTO.class);
            
            if (product == null) {
                throw new IllegalArgumentException("Product not found");
            }
            
            // Check if product is in stock
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock available");
            }
            
            // Check if product already exists in cart
            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst();
            
            if (existingItem.isPresent()) {
                // Update quantity if product already exists
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + quantity);
            } else {
                // Add new item if product doesn't exist in cart
                CartItem newItem = new CartItem(cart, productId, product.getName(), product.getPrice(), quantity, product.getImageUrl());
                cart.addItem(newItem);
            }
            
            cart.recalculateTotal();
            return cartRepository.save(cart);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error adding product to cart: " + e.getMessage());
        }
    }
    
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        Cart cart = getOrCreateCart(userId);
        
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));
        
        // Get product details to check stock
        String productServiceUrl = "http://product-service/api/products/" + productId;
        try {
            ProductDTO product = restTemplate.getForObject(productServiceUrl, ProductDTO.class);
            
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock available");
            }
            
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            cart.recalculateTotal();
            return cartRepository.save(cart);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating cart item: " + e.getMessage());
        }
    }
    
    @Transactional
    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));
        
        cart.removeItem(item);
        return cartRepository.save(cart);
    }
    
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.clear();
        return cartRepository.save(cart);
    }
    
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user"));
    }
    
    // DTO for Product Service response
    public static class ProductDTO {
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer stockQuantity;
        private String imageUrl;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
