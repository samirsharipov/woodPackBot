package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.ermatov.woodpack.event.BotDeleteEvent;
import uz.ermatov.woodpack.event.BotMessageEvent;

@Service
@RequiredArgsConstructor
public class TelegramBotController {

    private final ApplicationEventPublisher eventPublisher;

    public void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessage(long chatId, String text, ReplyKeyboard replyMarkup) {
        eventPublisher.publishEvent(new BotMessageEvent(this, chatId, text, replyMarkup));
    }

    public void deleteMessage(long chatId, int messageId) {
        eventPublisher.publishEvent(new BotDeleteEvent(this, chatId, messageId));
    }

}
