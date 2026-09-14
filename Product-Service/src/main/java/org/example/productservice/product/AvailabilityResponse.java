package org.example.productservice.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer requestedQuantity;
    private Integer availableStock;
}
