package be.nexios.orders.service;

import be.nexios.orders.model.OrderStatus;

public class OrderStatusConflictException extends RuntimeException {

    public OrderStatusConflictException(String action, OrderStatus current) {
        super("Cannot " + action + " an order with status " + current);
    }
}
