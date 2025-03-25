package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uz.ermatov.woodpack.service.ProductService;
import uz.ermatov.woodpack.service.UserStateService;

@Component
@RequiredArgsConstructor
public class UserCommandHandler {
    private final UserStateService userStateService;
    private final ProductService productService;
    private final TelegramBotController botController;

    public void handleUserCommand(long chatId) {
        String state = userStateService.getState(chatId);
        if (state == null) {
            botController.sendMessage(chatId, "Iltimos, oldin /start bosing.");
            return;
        }

        switch (state) {
            case "CHOOSE_PRODUCT" -> productService.chooseProduct(chatId);
            case "ENTER_NAME" -> botController.sendMessage(chatId, "Ismingizni kiriting:");
            default -> botController.sendMessage(chatId, "Noma'lum buyruq.");
        }
    }

    public void handleContactMessage(long chatId) {
        botController.sendMessage(chatId, "📞 Kontakt qabul qilindi. Mahsulot tanlang:");
        userStateService.saveState(chatId, "CHOOSE_PRODUCT");
        productService.chooseProduct(chatId);
    }
}
