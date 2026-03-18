package com.example.order;

/**
 * 订单状态
 */
public enum OrderStatus {
    /** 待支付 */
    PENDING,
    
    /** 已支付 */
    PAID,
    
    /** 处理中 */
    PROCESSING,
    
    /** 已发货 */
    SHIPPED,
    
    /** 已完成 */
    COMPLETED,
    
    /** 已取消 */
    CANCELLED,
    
    /** 退款中 */
    REFUNDING,
    
    /** 已退款 */
    REFUNDED
}