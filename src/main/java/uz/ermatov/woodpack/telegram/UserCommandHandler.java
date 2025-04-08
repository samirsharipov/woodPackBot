package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.ermatov.woodpack.buttons.KeyboardUtils;
import uz.ermatov.woodpack.service.ProductService;
import uz.ermatov.woodpack.service.UserStateService;


@Component
@RequiredArgsConstructor
public class UserCommandHandler {
    private final UserStateService userStateService;
    private final ProductService productService;
    private final TelegramBotController botController;
    private final KeyboardUtils keyboardUtils;
    private final Messages messages;

    public void handleUserCommand(long chatId, String messageText, int messageId) {
        String state = userStateService.getState(chatId);
        if (state == null) {
            botController.sendMessage(chatId, "Iltimos, oldin /start bosing.");
            return;
        }

        switch (state) {
            case "CHOOSE_LANGUAGE" -> updateMessage(chatId, messageText, messageId);
            case "CHOOSE_PRODUCT" -> productService.chooseProduct(chatId);
            case "ENTER_NAME" -> botController.sendMessage(chatId, "Ismingizni kiriting:");
            default -> botController.sendMessage(chatId, "Noma'lum buyruq.");
        }
    }

    public void handleContactMessage(long chatId, Contact contact) {
        botController.sendMessage(chatId, messages.getMessage(chatId,"done_contact"));
        userStateService.saveState(chatId, "CHOOSE_PRODUCT");
        productService.chooseProduct(chatId);
    }

    public void startUserCommand(long chatId) {
        userStateService.saveState(chatId, "CHOOSE_LANGUAGE");
        botController.sendMessage(chatId, messages.getMessage(chatId, "choose_language"), KeyboardUtils.getLanguageKeyboard());
    }

    public void updateMessage(long chatId, String messageText, int messageId) {
        String lang;
        switch (messageText) {
            case "🇬🇧 English" -> lang = "en";
            case "🇷🇺 Русский" -> lang = "ru";
            default -> lang = "uz";
        }
        userStateService.saveLanguage(chatId, lang);
        userStateService.saveState(chatId, "SEND_CONTACT");
        botController.sendMessage(chatId, messages.getMessage(chatId, "enter_phone"), keyboardUtils.getPhoneNumberKeyboard(chatId));
    }
}
