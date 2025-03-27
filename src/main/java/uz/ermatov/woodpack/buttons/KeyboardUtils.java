package uz.ermatov.woodpack.buttons;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.ermatov.woodpack.telegram.Messages;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyboardUtils {

    private final Messages messages;

    // 🔹 **Til tanlash uchun reply keyboard**
    public static ReplyKeyboardMarkup getLanguageKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🇺🇿 O‘zbekcha"));
        row1.add(new KeyboardButton("🇷🇺 Русский"));
        row1.add(new KeyboardButton("🇬🇧 English"));

        keyboardRows.add(row1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }


    public static ReplyKeyboardRemove removeKeyboard() {
        return new ReplyKeyboardRemove(true);
    }


    public ReplyKeyboardMarkup getPhoneNumberKeyboard(Long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(messages.getMessage(chatId, "button_send_contact"));
        button.setRequestContact(true); // 📌 Foydalanuvchi contact yuborishiga ruxsat beradi
        row.add(button);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup getAdminCrudKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("➕ Admin qo‘shish"));
        row1.add(new KeyboardButton("📋 Adminlar ro‘yxati"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("🗑 Adminni o‘chirish"));

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }


    public static ReplyKeyboardMarkup getAdminMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Kichraytirilgan klaviatura
        keyboardMarkup.setOneTimeKeyboard(false); // Doimiy klaviatura

        List<KeyboardRow> keyboard = new ArrayList<>();

        // 1-qator
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("➕ Mahsulot qo'shish"));  // Product CRUD
        row1.add(new KeyboardButton("📋 Mahsulotlar ro‘yxati"));

        // 2-qator
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("✏ Mahsulotni tahrirlash"));
        row2.add(new KeyboardButton("🗑 Mahsulotni o‘chirish"));

        // 3-qator
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("📊 Statistikalar"));  // Admin uchun statistika
        row3.add(new KeyboardButton("🔑 Admin qo‘shish"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

}