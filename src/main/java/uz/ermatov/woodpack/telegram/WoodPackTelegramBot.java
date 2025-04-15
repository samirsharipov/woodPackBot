package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ermatov.woodpack.event.BotDeleteEvent;
import uz.ermatov.woodpack.event.BotMessageEvent;
import uz.ermatov.woodpack.service.AdminService;
import uz.ermatov.woodpack.service.HandleCallbackQueryService;


@Component
@RequiredArgsConstructor
public class WoodPackTelegramBot extends TelegramLongPollingBot {

    private final AdminService adminService;
    private final UserCommandHandler userCommandHandler;
    private final AdminCommandHandler adminCommandHandler;
    private final HandleCallbackQueryService handleCallbackQueryService;

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
        if (update.hasCallbackQuery()) {
            handleCallbackQueryService.handleCallbackQuery(update.getCallbackQuery());
        } else {

            long chatId = update.getMessage().getChatId();
            if (update.getMessage().hasContact()) {
                userCommandHandler.handleContactMessage(chatId,update.getMessage().getContact());
                return;
            }

            if (update.getMessage().hasText()) {
                Integer messageId = update.getMessage().getMessageId();
                String messageText = update.getMessage().getText();
                if (adminService.isAdmin(chatId) && adminCommandHandler != null) {
                    if ("/start".equals(messageText)) {
                        adminCommandHandler.sendWelcomeMessage(chatId);
                    } else {
                        adminCommandHandler.handleAdminCommand(chatId, messageText, messageId);
                    }
                } else {
                    if ("/start".equals(messageText)) {
                        userCommandHandler.startUserCommand(chatId);
                    } else {
                        userCommandHandler.handleUserCommand(chatId,messageText,messageId);
                    }
                }
            }
        }
    }


    @EventListener
    public void onBotMessageEvent(BotMessageEvent event) {
        SendMessage message = new SendMessage(String.valueOf(event.getChatId()), event.getText());
        if (event.getReplyKeyboard() != null) {
            message.setReplyMarkup(event.getReplyKeyboard());
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("❌ Xatolik: Xabar jo‘natib bo‘lmadi! " + e.getMessage());
            e.printStackTrace();
        }
    }


    @EventListener
    public void handleDeleteMessage(BotDeleteEvent event) {
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(event.getChatId()), event.getMessageId());

        try {
            execute(deleteMessage); // Bot xabarni o'chiradi
        } catch (Exception e) {
            System.err.println("❌ Xatolik: Xabarni o‘chirib bo‘lmadi! " + e.getMessage());
            e.printStackTrace();
        }
    }
}