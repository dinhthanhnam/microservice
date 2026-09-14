package org.example.productservice.product;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    public List<ProductResponse> list() {
        return products.findAll().stream().map(ProductResponse::from).toList();
    }

    public ProductResponse get(Long id) {
        return ProductResponse.from(find(id));
    }

    public ProductResponse create(CreateProductRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()
                || request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0
                || request.getStock() == null || request.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "name, non-negative price and non-negative stock are required");
        }
        return ProductResponse.from(products.save(new Product(request.getName(), request.getPrice(), request.getStock())));
    }

    public AvailabilityResponse checkAvailability(Long id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be greater than 0");
        }
        Product product = find(id);
        if (product.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "insufficient stock: available=" + product.getStock());
        }
        return new AvailabilityResponse(product.getId(), product.getName(), product.getPrice(),
                quantity, product.getStock());
    }

    private Product find(Long id) {
        return products.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found: " + id));
    }
}
