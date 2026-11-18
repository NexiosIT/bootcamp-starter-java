package be.nexios.orders.model;

import java.time.Instant;

public class Order {

    private Long id;
    private String description;
    private OrderStatus status;
    private final Instant createdAt;

    public Order(Long id, String description, OrderStatus status) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
