package org.example.orderservice.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String status;
    private Instant createdAt;

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getProductId(), order.getProductName(),
                order.getQuantity(), order.getUnitPrice(), order.getTotalPrice(), order.getStatus(),
                order.getCreatedAt());
    }
}
