package be.nexios.orders.repository;

import be.nexios.orders.model.Order;
import be.nexios.orders.model.OrderStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class OrderDataLoader implements CommandLineRunner {

    private final OrderRepository repository;

    OrderDataLoader(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.save(new Order(null, "MacBook Pro", OrderStatus.ORDERED));
        repository.save(new Order(null, "iPhone", OrderStatus.SHIPPED));
    }
}
