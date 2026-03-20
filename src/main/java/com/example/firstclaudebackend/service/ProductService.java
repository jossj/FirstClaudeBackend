package com.example.firstclaudebackend.service;

import com.example.firstclaudebackend.exception.ResourceNotFoundException;
import com.example.firstclaudebackend.model.Product;
import com.example.firstclaudebackend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public List<Product> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product create(Product product) {
        return repository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setQuantity(updated.getQuantity());
        return repository.save(existing);
    }

    public void delete(Long id) {
        findById(id); // throws if not found
        repository.deleteById(id);
    }
}
