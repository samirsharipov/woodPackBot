package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.ermatov.woodpack.buttons.InlineKeyboardUtils;
import uz.ermatov.woodpack.buttons.KeyboardUtils;
import uz.ermatov.woodpack.model.Product;
import uz.ermatov.woodpack.repository.ProductRepository;
import uz.ermatov.woodpack.telegram.TelegramBotController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserStateService userStateService;
    private final TelegramBotController botController;
    private final InlineKeyboardUtils keyboardUtils;


    public void getAllProducts(long chatId, int page, int messageId) {
        List<Product> all = productRepository.findAll();

        if (all.isEmpty()) {
            botController.sendMessage(chatId, "Mahsulot mavjud emas!");
            userStateService.saveState(chatId, "START");
            return;
        }
        botController.sendMessage(chatId, "📦 Kerakli mahsulotni tanlang:", keyboardUtils.getProductListKeyboard(page, all));

        userStateService.saveState(chatId, "GET_PRODUCTS");
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public void getProductById(long chatId, Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            String productInfo = "📦 Mahsulot ma’lumotlari:\n" +
                    "🔹 Nomi: " + product.getName() + "\n" +
                    "💰 Narxi: " + product.getPrice() + " so‘m";

            botController.sendMessage(chatId, productInfo, InlineKeyboardUtils.getProductActionsInlineKeyboard(productId));
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
            String productInfo = "✅ Mahsulot saqlandi!:\n" +
                    "🔹 Nomi: " + product.getName() + "\n" +
                    "💰 Narxi: " + product.getPrice() + " so‘m";
            botController.sendMessage(chatId, productInfo, KeyboardUtils.getAdminMenuKeyboard());
            userStateService.saveState(chatId, "START");
        } catch (NumberFormatException e) {
            botController.sendMessage(chatId, "Mahsulotning narxini biriktirishda xatolik yuzaga keldi!");
        }
    }

    public void updateProduct(Long chatId) {

        String productId = userStateService.getTempData(chatId, "PRODUCT_ID");
        String productPrice = userStateService.getTempData(chatId, "PRODUCT_PRICE");
        String productName = userStateService.getTempData(chatId, "PRODUCT_NAME");


        productRepository.findById(Long.parseLong(productId))
                .map(existingProduct -> {
                    existingProduct.setName(productName);
                    existingProduct.setPrice(Double.valueOf(productPrice));
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi"));

        botController.sendMessage(chatId, "Mahsulot taxrirlandi! \n" +
                "Mahsulot nomi: " + productName + "\n" +
                "Mahsulot narxi: " + productPrice + " so'm");
        userStateService.saveState(chatId, "START");
    }

    public void chooseProduct(long chatId) {

    }

    public void delete(long productId, long chatId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            botController.sendMessage(chatId, "Mahsulot allaqachon o‘chirilgan!");
        } else {
            productRepository.deleteById(productId);
            botController.sendMessage(chatId, "Mahsulot o‘chirildi ✅");
        }
    }

    public void rejectDelete(long productId, long chatId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            botController.sendMessage(chatId, "Mahsulot allaqachon o‘chirilgan!");
        } else {
            botController.sendMessage(chatId, "Bekor qilindi ❌");
        }
    }
}