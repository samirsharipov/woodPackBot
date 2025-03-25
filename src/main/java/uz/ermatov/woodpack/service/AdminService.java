package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ermatov.woodpack.model.Admin;
import uz.ermatov.woodpack.model.Product;
import uz.ermatov.woodpack.repository.AdminRepository;
import uz.ermatov.woodpack.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final ProductRepository productRepository;

    public boolean isAdmin(Long telegramId) {
        return adminRepository.findByTelegramId(telegramId).isPresent();
    }

    public boolean checkPassword(Long telegramId, String password) {
        Optional<Admin> admin = adminRepository.findByTelegramId(telegramId);
        return admin.isPresent() && admin.get().getPassword().equals(password);
    }

    public boolean addAdmin(Long telegramId) {
        if (!isAdmin(telegramId)) {
            adminRepository.save(new Admin(null, null, telegramId, null));
            return true;
        }
        return false;
    }

    public void removeAdmin(Long telegramId) {
        adminRepository.findByTelegramId(telegramId)
                .ifPresent(adminRepository::delete);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public boolean addProduct(String name, double price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        productRepository.save(product);
        return true;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
