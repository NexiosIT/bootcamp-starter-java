package be.nexios.orders.repository;

import be.nexios.orders.model.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Order> findAll() {
        List<Order> all = new ArrayList<>(orders.values());
        all.sort((a, b) -> a.getId().compareTo(b.getId()));
        return all;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(nextId.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }
}
