package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ermatov.woodpack.service.UserStateService;
import uz.ermatov.woodpack.utils.KeyboardUtils;
import uz.ermatov.woodpack.utils.Messages;

@Component
@RequiredArgsConstructor
public class WoodPackTelegramBot extends TelegramLongPollingBot {
    private final UserStateService userStateService;
    private final Messages messages;
    private final KeyboardUtils keyboardUtils;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();

            // 📌 Agar foydalanuvchi contact jo‘natsa
            if (update.getMessage().hasContact()) {
                handleContactMessage(chatId, update.getMessage().getContact());
                return;
            }

            // 📌 Agar foydalanuvchi oddiy matn jo‘natsa
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                if (messageText.equals("/start")) {
                    sendLanguageSelection(chatId);
                    userStateService.saveState(chatId, "CHOOSING_LANGUAGE");
                } else {
                    processUserInput(chatId, messageText);
                }
            }
        }
    }

    private void processUserInput(long chatId, String messageText) {
        String state = userStateService.getState(chatId);
        String lang = userStateService.getLanguage(chatId);

        if (state == null) {
            sendLanguageSelection(chatId);
            return;
        }

        switch (state) {
            case "CHOOSING_LANGUAGE":
                switch (messageText) {
                    case "🇺🇿 O‘zbekcha" -> userStateService.saveLanguage(chatId, "uz");
                    case "🇷🇺 Русский" -> userStateService.saveLanguage(chatId, "ru");
                    case "🇬🇧 English" -> userStateService.saveLanguage(chatId, "en");
                    default -> {
                        sendMessage(chatId, messages.getMessage(chatId, "invalid_choice"));
                        return;
                    }
                }
                sendMessage(chatId, messages.getMessage(chatId, "enter_name"));
                userStateService.saveState(chatId, "ENTER_NAME");
                break;

            case "ENTER_NAME":
                sendContactButton(chatId);
                userStateService.saveState(chatId, "ENTER_PHONE");
                break;

            case "CHOOSE_PRODUCT":
                if (messageText.matches("📦 Kopli|🍾 Chupa|📦 Karobkali")) {
                    sendMessage(chatId, messages.getMessage(chatId, "order_received"));
                    userStateService.saveState(chatId, "FINISHED");
                } else {
                    sendMessage(chatId, messages.getMessage(chatId, "invalid_choice"));
                }
                break;
        }
    }

    private void sendLanguageSelection(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messages.getMessage(chatId, "choose_language"));
        message.setReplyMarkup(KeyboardUtils.getLanguageKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendContactButton(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messages.getMessage(chatId, "enter_phone"));
        message.setReplyMarkup(keyboardUtils.getPhoneNumberKeyboard(chatId));

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleContactMessage(long chatId, Contact contact) {
        String phoneNumber = contact.getPhoneNumber();
//        userStateService.savePhoneNumber(chatId, phoneNumber);

        sendMessage(chatId, messages.getMessage(chatId, "choose_product"));
        userStateService.saveState(chatId, "CHOOSE_PRODUCT");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}