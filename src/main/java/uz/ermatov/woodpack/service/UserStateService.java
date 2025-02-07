package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserStateService {
    private final StringRedisTemplate redisTemplate;
    private static final String STATE_PREFIX = "user:state:";
    private static final String LANG_PREFIX = "user:lang:";
    private static final long EXPIRE_DAYS = 7; // Ma'lumotlarni 7 kun saqlaymiz

    // ✅ Foydalanuvchi state ni saqlash (7 kun saqlanadi)
    public void saveState(Long chatId, String state) {
        String key = STATE_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, state, Duration.ofDays(EXPIRE_DAYS));
    }

    // ✅ Foydalanuvchi state ni olish
    public String getState(Long chatId) {
        String key = STATE_PREFIX + chatId;
        return redisTemplate.opsForValue().get(key);
    }

    // ✅ Foydalanuvchi state ni o‘chirish
    public void deleteState(Long chatId) {
        String key = STATE_PREFIX + chatId;
        redisTemplate.delete(key);
    }

    // ✅ Foydalanuvchi tilini saqlash (7 kun saqlanadi)
    public void saveLanguage(Long chatId, String language) {
        String key = LANG_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, language, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    // ✅ Foydalanuvchi tilini olish (default "uz")
    public String getLanguage(Long chatId) {
        String key = LANG_PREFIX + chatId;
        String lang = redisTemplate.opsForValue().get(key);
        return lang != null ? lang : "uz";  // `getOrDefault` o‘rniga
    }

    // ✅ Foydalanuvchi tilini o‘chirish
    public void deleteLanguage(Long chatId) {
        String key = LANG_PREFIX + chatId;
        redisTemplate.delete(key);
    }
}