package com.example.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param order 订单信息
     * @return 订单ID
     */
    String createOrder(Order order);
    
    /**
     * 获取订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order getOrder(String orderId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     */
    void updateOrderStatus(String orderId, OrderStatus status);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getUserOrders(String userId);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     */
    void cancelOrder(String orderId);
    
    /**
     * 计算订单总额
     * @param orderId 订单ID
     * @return 订单总额
     */
    BigDecimal calculateTotal(String orderId);
}