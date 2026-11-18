package be.nexios.orders.service;

import be.nexios.orders.model.Order;
import be.nexios.orders.model.OrderStatus;
import be.nexios.orders.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order create(String description) {
        if (description == null || description.isBlank()) {
            throw new OrderValidationException("Description is required");
        }
        return repository.save(new Order(null, description, OrderStatus.ORDERED));
    }

    public Order ship(Long id) {
        return transition(id, "ship", OrderStatus.ORDERED, OrderStatus.SHIPPED);
    }

    public Order deliver(Long id) {
        return transition(id, "deliver", OrderStatus.SHIPPED, OrderStatus.DELIVERED);
    }

    public Order cancel(Long id) {
        return transition(id, "cancel", OrderStatus.ORDERED, OrderStatus.CANCELLED);
    }

    private Order transition(Long id, String action, OrderStatus from, OrderStatus to) {
        Order order = findById(id);
        if (order.getStatus() != from) {
            throw new OrderStatusConflictException(action, order.getStatus());
        }
        order.setStatus(to);
        return order;
    }
}
