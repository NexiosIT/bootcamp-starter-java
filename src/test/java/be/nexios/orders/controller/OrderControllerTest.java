package be.nexios.orders.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import be.nexios.orders.model.Order;
import be.nexios.orders.model.OrderStatus;
import be.nexios.orders.service.OrderNotFoundException;
import be.nexios.orders.service.OrderService;
import be.nexios.orders.service.OrderStatusConflictException;
import be.nexios.orders.service.OrderValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldReturnAllOrders_whenGetOrders() {
        given(orderService.findAll())
                .willReturn(List.of(
                        new Order(1L, "MacBook Pro", OrderStatus.ORDERED),
                        new Order(2L, "iPhone", OrderStatus.SHIPPED)));

        MvcTestResult result = mvc.get().uri("/orders").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.length()").isEqualTo(2);
        assertThat(result).bodyJson().extractingPath("$[0].description").isEqualTo("MacBook Pro");
        assertThat(result).bodyJson().extractingPath("$[1].status").isEqualTo("SHIPPED");
    }

    @Test
    void shouldReturn404_whenOrderDoesNotExist() {
        given(orderService.findById(99L)).willThrow(new OrderNotFoundException(99L));

        MvcTestResult result = mvc.get().uri("/orders/99").exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyText().isEqualTo("Could not find order 99");
    }

    @Test
    void shouldReturn409_whenShippingOrderThatIsNotOrdered() {
        given(orderService.ship(2L)).willThrow(new OrderStatusConflictException("ship", OrderStatus.SHIPPED));

        MvcTestResult result = mvc.put().uri("/orders/2/ship").exchange();

        assertThat(result).hasStatus(HttpStatus.CONFLICT);
        assertThat(result).bodyText().isEqualTo("Cannot ship an order with status SHIPPED");
    }

    @Test
    void shouldReturn400_whenDescriptionIsMissing() {
        given(orderService.create(null)).willThrow(new OrderValidationException("Description is required"));

        MvcTestResult result = mvc.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyText().isEqualTo("Description is required");
    }
}
