package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.ermatov.woodpack.buttons.KeyboardUtils;
import uz.ermatov.woodpack.service.AdminService;
import uz.ermatov.woodpack.service.ProductService;
import uz.ermatov.woodpack.service.UserStateService;

@Component
@RequiredArgsConstructor
public class AdminCommandHandler {
    private final UserStateService userStateService;
    private final TelegramBotController botController;
    private final ProductService productService;
    private final AdminService adminService;

    public void handleAdminCommand(long chatId, String messageText, int messageId) {
        switch (messageText) {
            case "🔑 Admin qo‘shish" -> {
                userStateService.saveState(chatId, "ADD_ADMIN_NAME");
                botController.sendMessage(chatId, "Admin ismini kiriting:");
            }
            case "📋 Adminlar ro‘yxati" -> {
                userStateService.saveLanguage(chatId, "GET_ADMIN_LIST");
                adminService.getAllAdmins(chatId);
            }
            case "➕ Mahsulot qo'shish" -> {
                userStateService.saveState(chatId, "ADD_PRODUCT_NAME");
                botController.sendMessage(chatId, "Mahsulot nomini kiriting:", KeyboardUtils.removeKeyboard());
            }
            case "📋 Mahsulotlar ro‘yxati" -> {
                int page = userStateService.getPage(chatId); // Admin uchun sahifa raqamini olish
                productService.getAllProducts(chatId, page);
            }
            default -> updateMessages(chatId, messageText);
        }
    }

    public void sendWelcomeMessage(long chatId) {
        String welcomeText = "Xush kelibsiz, admin! Quyidagi tugmalardan foydalaning:";
        // Yangi inline keyboardni qo‘shish
        botController.sendMessage(chatId, welcomeText, KeyboardUtils.getAdminMenuKeyboard());
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
            case "UPDATE_PRODUCT" -> {
                userStateService.saveTempData(chatId, "PRODUCT_NAME", messageText);
                userStateService.saveState(chatId, "UPDATE_PRODUCT_PRICE");
                botController.sendMessage(chatId, "Mahsulot narxini kiriting:");
            }
            case "UPDATE_PRODUCT_PRICE" -> {
                userStateService.saveTempData(chatId, "PRODUCT_PRICE", messageText);
                userStateService.saveState(chatId, "START");
                productService.updateProduct(chatId);
            }
            case "ADD_ADMIN_NAME" -> {
                userStateService.saveTempData(chatId, "ADD_ADMIN_NAME", messageText);
                botController.sendMessage(chatId, "Admin ID kiriting:");
                userStateService.saveState(chatId, "ADD_ADMIN_ID");
            }
            case "ADD_ADMIN_ID" -> {
                userStateService.saveTempData(chatId, "ADD_ADMIN_ID", messageText);
                adminService.addAdmin(chatId);
            }
        }
    }
}
