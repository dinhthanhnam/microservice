package org.example.productservice.product;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class ProductDataInitializer {
    @Bean
    CommandLineRunner seedProducts(ProductRepository products) {
        return args -> {
            if (products.count() == 0) {
                products.save(new Product("Mechanical Keyboard", new BigDecimal("89.90"), 10));
                products.save(new Product("Wireless Mouse", new BigDecimal("29.90"), 2));
            }
        };
    }
}
