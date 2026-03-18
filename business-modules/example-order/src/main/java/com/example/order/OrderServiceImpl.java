package com.example.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
public class OrderServiceImpl implements OrderService {
    
    private final Map<String, Order> orderStore = new HashMap<>();
    private int nextOrderId = 1;
    
    @Override
    public String createOrder(Order order) {
        String orderId = "ORD-" + nextOrderId++;
        order.setOrderId(orderId);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 计算订单总额
        if (order.getItems() != null) {
            BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(total);
        } else {
            order.setTotalAmount(BigDecimal.ZERO);
        }
        
        orderStore.put(orderId, order);
        return orderId;
    }
    
    @Override
    public Order getOrder(String orderId) {
        return orderStore.get(orderId);
    }
    
    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderStore.get(orderId);
        if (order != null) {
            order.setStatus(status);
        }
    }
    
    @Override
    public List<Order> getUserOrders(String userId) {
        return orderStore.values().stream()
            .filter(order -> userId.equals(order.getUserId()))
            .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
            .collect(Collectors.toList());
    }
    
    @Override
    public void cancelOrder(String orderId) {
        Order order = orderStore.get(orderId);
        if (order != null && order.getStatus() != OrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.CANCELLED);
        }
    }
    
    @Override
    public BigDecimal calculateTotal(String orderId) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            return BigDecimal.ZERO;
        }
        
        if (order.getTotalAmount() != null) {
            return order.getTotalAmount();
        }
        
        // 重新计算
        if (order.getItems() != null) {
            BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(total);
            return total;
        }
        
        return BigDecimal.ZERO;
    }
}