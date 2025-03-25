package uz.ermatov.woodpack.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Getter
public class BotMessageEvent extends ApplicationEvent {
    private final long chatId;
    private final String text;
    private final ReplyKeyboard replyKeyboard;

    public BotMessageEvent(Object source, long chatId, String text, ReplyKeyboard replyKeyboard) {
        super(source);
        this.chatId = chatId;
        this.text = text;
        this.replyKeyboard = replyKeyboard;
    }
}