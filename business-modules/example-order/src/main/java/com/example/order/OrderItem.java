package com.example.order;

import java.math.BigDecimal;

/**
 * 订单项实体类
 */
public class OrderItem {
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    
    public OrderItem() {
    }
    
    public OrderItem(String productId, String productName, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    /**
     * 计算此项总价
     */
    public BigDecimal getSubtotal() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    public String toString() {
        return "OrderItem{productId='" + productId + "', productName='" + productName + 
               "', price=" + price + ", quantity=" + quantity + "}";
    }
}