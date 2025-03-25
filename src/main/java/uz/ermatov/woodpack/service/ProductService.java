package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ermatov.woodpack.model.Product;
import uz.ermatov.woodpack.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
            .map(existingProduct -> {
                existingProduct.setName(updatedProduct.getName());
                existingProduct.setPrice(updatedProduct.getPrice());
                return productRepository.save(existingProduct);
            })
            .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi"));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void chooseProduct(long chatId) {

    }
}