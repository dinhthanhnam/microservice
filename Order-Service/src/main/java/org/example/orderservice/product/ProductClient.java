package org.example.orderservice.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}/availability")
    AvailabilityResponse checkAvailability(@PathVariable("id") Long id,
                                            @RequestParam("quantity") Integer quantity);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AvailabilityResponse {
        private Long productId;
        private String productName;
        private BigDecimal unitPrice;
        private Integer requestedQuantity;
        private Integer availableStock;
    }
}
