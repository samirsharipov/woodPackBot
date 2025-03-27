package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ermatov.woodpack.buttons.InlineKeyboardUtils;
import uz.ermatov.woodpack.model.Product;
import uz.ermatov.woodpack.repository.ProductRepository;
import uz.ermatov.woodpack.telegram.TelegramBotController;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserStateService userStateService;
    private final TelegramBotController botController;
    private final InlineKeyboardUtils keyboardUtils;

    public void getAllProducts(long chatId) {
        List<Product> all = productRepository.findAll();
        if (all.isEmpty()) {
            botController.sendMessage(chatId, "Mahsulot mavjud emas!");
            userStateService.saveState(chatId, "START");
            return;
        }

        botController.sendMessage(chatId, "📦 Kerakli mahsulotni tanlang:", keyboardUtils.getProductListInlineKeyboard(all,chatId));
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public void getProductById(long chatId, Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            String productInfo = "📦 Mahsulot ma’lumotlari:\n" +
                                 "🔹 Nomi: " + product.getName() + "\n" +
                                 "💰 Narxi: " + product.getPrice() + " so‘m";

            botController.sendMessage(chatId, productInfo, keyboardUtils.getProductActionsInlineKeyboard(productId));
        } else {
            botController.sendMessage(chatId, "❌ Mahsulot topilmadi!");
        }
    }

    public void addProduct(long chatId) {
        try {
            Product product = new Product(
                    userStateService.getTempData(chatId, "PRODUCT_NAME"),
                    Double.valueOf(userStateService.getTempData(chatId, "PRODUCT_PRICE")));
            productRepository.save(product);
            botController.sendMessage(chatId, "Mahsulot saqlandi! \n" +
                    "Mahsulot nomi : " + product.getName() + " \n" +
                    "Mahsulot narxi : " + product.getPrice() + " so'm");
            userStateService.saveState(chatId, "START");
        } catch (NumberFormatException e) {
            botController.sendMessage(chatId, "Mahsulotning narxini biriktirishda xatolik yuzaga keldi!");
        }
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