package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.ermatov.woodpack.buttons.KeyboardUtils;
import uz.ermatov.woodpack.service.ProductService;
import uz.ermatov.woodpack.service.UserStateService;

@Component
@RequiredArgsConstructor
public class AdminCommandHandler {
    private final UserStateService userStateService;
    private final TelegramBotController botController;
    private final ProductService productService;

    public void handleAdminCommand(long chatId, String messageText) {
        switch (messageText) {
            case "🔑 Admin qo‘shish" -> {
                userStateService.saveState(chatId, "ADD_ADMIN_ID");
                botController.sendMessage(chatId, "Admin ID kiriting:");
            }
            case "➕ Mahsulot qo'shish" -> {
                userStateService.saveState(chatId, "ADD_PRODUCT_NAME");
                botController.sendMessage(chatId, "Mahsulot nomini kiriting:", KeyboardUtils.removeKeyboard());
            }
            case "📋 Mahsulotlar ro‘yxati" -> productService.getAllProducts(chatId);
            default ->
                    updateMessages(chatId, messageText);
        }
    }

    public void sendWelcomeMessage(long chatId) {
        String welcomeText = "Xush kelibsiz, admin! Quyidagi tugmalardan foydalaning:";
        // Yangi inline keyboardni qo‘shish
        botController.sendMessage(chatId, welcomeText, KeyboardUtils.getAdminMenuKeyboard());
    }

    private void sendProductList(long chatId) {
        botController.sendMessage(chatId, "📦 Mahsulotlar ro‘yxati (hali implementatsiya qilinmagan).");
    }

    private void updateMessages(long chatId, String messageText) {
        String state = userStateService.getState(chatId);
        switch (state) {
            case "ADD_PRODUCT_NAME" -> {
                userStateService.saveTempData(chatId, "PRODUCT_NAME", messageText);
                userStateService.saveState(chatId, "ADD_PRODUCT_PRICE");
                botController.sendMessage(chatId, "Mahsulot narxini kiriting:");
            }
            case "ADD_PRODUCT_PRICE" -> {
                userStateService.saveTempData(chatId, "PRODUCT_PRICE", messageText);
                productService.addProduct(chatId);
            }
        }
    }
}
