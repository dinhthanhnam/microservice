package org.example.orderservice.order;

import feign.FeignException;
import org.example.orderservice.product.ProductClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final ProductClient products;

    public OrderService(OrderRepository orders, ProductClient products) {
        this.orders = orders;
        this.products = products;
    }

    public OrderResponse create(CreateOrderRequest request) {
        if (request == null || request.getProductId() == null || request.getQuantity() == null
                || request.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "productId and a positive quantity are required");
        }

        ProductClient.AvailabilityResponse product;
        try {
            product = products.checkAvailability(request.getProductId(), request.getQuantity());
        } catch (FeignException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "product not found: " + request.getProductId());
        } catch (FeignException.Conflict ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "insufficient stock");
        }

        Order order = orders.save(new Order(product.getProductId(), product.getProductName(),
                product.getRequestedQuantity(), product.getUnitPrice()));
        return OrderResponse.from(order);
    }
}
