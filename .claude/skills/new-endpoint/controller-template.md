# Controller-template

Controller (`controller/OrderController.java`): enkel HTTP, delegeert naar de service.

```java
@PutMapping("/{id}/<actie>")
public Order <actie>(@PathVariable Long id) {
    return orderService.<actie>(id);
}
```

Service (`service/OrderService.java`): logica en statuscheck, gooit een exception bij een fout.

```java
public Order <actie>(Long id) {
    return transition(id, "<actie>", OrderStatus.<VAN>, OrderStatus.<NAAR>);
}
```

- Niet gevonden: `OrderNotFoundException`, wordt 404
- Ongeldige status: `OrderStatusConflictException`, wordt 409
- Ongeldige input: `OrderValidationException`, wordt 400
- Nieuwe fout: eigen exception in `service/` plus een handler in `controller/OrderExceptionHandler.java`
- Request body: een `record` in `controller/`, zoals `CreateOrderRequest`
