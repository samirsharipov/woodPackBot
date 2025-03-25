package uz.ermatov.woodpack.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.ermatov.woodpack.service.UserStateService;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class Messages {
    private final MessageSource messageSource;
    private final UserStateService userStateService;

    public String getMessage(Long chatId, String key) {
        String lang = userStateService.getLanguage(chatId); // Foydalanuvchi tilini Redis'dan olish
        Locale locale = new Locale(lang);
        return messageSource.getMessage(key, null, locale);
    }
}