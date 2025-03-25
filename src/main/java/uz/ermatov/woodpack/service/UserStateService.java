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
    private static final String ID_PREFIX = "user:id:";
    private static final String TEMP_DATA_PREFIX = "user:temp:";
    private static final long EXPIRE_DAYS = 7; // Ma'lumotlarni 7 kun saqlaymiz

    // ✅ Foydalanuvchi state ni saqlash
    public void saveState(Long chatId, String state) {
        String key = STATE_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, state, Duration.ofDays(EXPIRE_DAYS));
    }

    // ✅ Foydalanuvchi state ni olish
    public String getState(Long chatId) {
        return redisTemplate.opsForValue().get(STATE_PREFIX + chatId);
    }

    // ✅ Foydalanuvchi state ni o‘chirish
    public void deleteState(Long chatId) {
        redisTemplate.delete(STATE_PREFIX + chatId);
    }

    // ✅ Foydalanuvchi tilini saqlash
    public void saveLanguage(Long chatId, String language) {
        redisTemplate.opsForValue().set(LANG_PREFIX + chatId, language, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    // ✅ Foydalanuvchi tilini olish (default "uz")
    public String getLanguage(Long chatId) {
        String language = redisTemplate.opsForValue().get(LANG_PREFIX + chatId);
        return (language != null) ? language : "uz";
    }

    // ✅ Foydalanuvchi tilini o‘chirish
    public void deleteLanguage(Long chatId) {
        redisTemplate.delete(LANG_PREFIX + chatId);
    }

    // ✅ Admin ID ni saqlash
    public void saveAdminId(Long chatId, String adminId) {
        redisTemplate.opsForValue().set(ID_PREFIX + chatId, adminId, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    // ✅ Admin ID ni olish
    public String getAdminId(Long chatId) {
        return redisTemplate.opsForValue().get(ID_PREFIX + chatId);
    }

    // ✅ Vaqtinchalik ma'lumotlarni saqlash
    public void saveTempData(Long chatId, String key, String value) {
        redisTemplate.opsForValue().set(TEMP_DATA_PREFIX + chatId + ":" + key, value, Duration.ofDays(EXPIRE_DAYS));
    }

    // ✅ Vaqtinchalik ma'lumotlarni olish
    public String getTempData(Long chatId, String key) {
        return redisTemplate.opsForValue().get(TEMP_DATA_PREFIX + chatId + ":" + key);
    }

    // ✅ Vaqtinchalik ma'lumotlarni o‘chirish
    public void deleteTempData(Long chatId, String key) {
        redisTemplate.delete(TEMP_DATA_PREFIX + chatId + ":" + key);
    }
}