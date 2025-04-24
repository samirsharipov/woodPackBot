package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.ermatov.woodpack.buttons.KeyboardUtils;
import uz.ermatov.woodpack.service.OrderService;
import uz.ermatov.woodpack.service.ProductService;
import uz.ermatov.woodpack.service.UserService;
import uz.ermatov.woodpack.service.UserStateService;


@Component
@RequiredArgsConstructor
public class UserCommandHandler {
    private final UserStateService userStateService;
    private final ProductService productService;
    private final TelegramBotController botController;
    private final KeyboardUtils keyboardUtils;
    private final Messages messages;
    private final UserService userService;
    private final OrderService orderService;


    public void handleUserTextCommand(long chatId, String messageText, int messageId, long telegramId) {
        String menuProduct = messages.getMessage(chatId, "menu_product");
        String order = messages.getMessage(chatId, "order");
        int page = userStateService.getPage(chatId);
        if (messageText.equals(menuProduct)) {
            productService.getAllProducts(chatId, page, true);
        } else if (messageText.equals(order)) {
            orderService.getAllByTelegramId(telegramId, chatId);
        } else {
            handleUserCommand(chatId, messageText, messageId);
        }
    }

    public void handleUserCommand(long chatId, String messageText, int messageId) {
        String state = userStateService.getState(chatId);
        int page = userStateService.getPage(chatId);
        if (state == null) {
            botController.sendMessage(chatId, "Iltimos, oldin /start bosing.");
            return;
        }

        switch (state) {
            case "CHOOSE_LANGUAGE" -> updateMessage(chatId, messageText, messageId);
            case "ENTER_NAME" -> botController.sendMessage(chatId, "Ismingizni kiriting:");
            case "SHOW_MENU" -> botController.sendMessage(chatId, messages.getMessage(chatId, "button_main_menu")
                    , keyboardUtils.getUserMenuKeyboard(chatId));
            default -> botController.sendMessage(chatId, "Noma'lum buyruq.");
        }
    }

    public void handleContactMessage(long chatId, Contact contact, long telegramId) {
        int page = userStateService.getPage(chatId);
        userService.save(contact, telegramId);
        botController.sendMessage(chatId, messages.getMessage(chatId, "done_contact"),
                keyboardUtils.getUserMenuKeyboard(chatId));
        userStateService.saveState(chatId, "SHOW_MENU");
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
        botController.sendMessage(chatId, messages.getMessage(chatId, "enter_phone"),
                keyboardUtils.getPhoneNumberKeyboard(chatId));
    }
}
