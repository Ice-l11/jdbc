package com.lyy.entity;

/**
 * @ClassName Order
 * @Description 订单实体类
 * @Author lyy
 * @Date 2022/9/13 16:16
 * @Version 1.0
 **/
public class Order {

    private Integer orderId;
    private String orderName;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderName='" + orderName + '\'' +
                '}';
    }
}
