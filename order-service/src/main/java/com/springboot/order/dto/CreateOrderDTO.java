package com.springboot.order.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderDTO {
    @NotBlank(message = "User email is required")
    private String userEmail;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemDTO> orderItems;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
