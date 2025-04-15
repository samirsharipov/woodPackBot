package uz.ermatov.woodpack.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BotDeleteEvent extends ApplicationEvent {

    private final Long chatId;
    private final Integer messageId;

    public BotDeleteEvent(Object source, Long chatId, Integer messageId) {
        super(source);
        this.chatId = chatId;
        this.messageId = messageId;
    }
}
