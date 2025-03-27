package uz.ermatov.woodpack.buttons;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.ermatov.woodpack.model.Product;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InlineKeyboardUtils {

    public InlineKeyboardMarkup getProductListInlineKeyboard(List<Product> products, long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Product product : products) {
            List<List<InlineKeyboardButton>> actionRow = getInlineKeyboardButtons(product);
            rows.addAll(actionRow);
        }

        // ➕ Mahsulot qo‘shish tugmasini oxiriga qo‘shish
        InlineKeyboardButton addProductButton = new InlineKeyboardButton();
        addProductButton.setText("➕ Mahsulot qo‘shish");
        addProductButton.setCallbackData("ADD_PRODUCT");

        List<InlineKeyboardButton> addProductRow = new ArrayList<>();
        addProductRow.add(addProductButton);
        rows.add(addProductRow);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private static List<List<InlineKeyboardButton>> getInlineKeyboardButtons(Product product) {
        InlineKeyboardButton productButton = new InlineKeyboardButton();
        productButton.setText("📦 " + product.getName());
        productButton.setCallbackData("PRODUCT_" + product.getId());

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Mahsulot nomini alohida chiqarish
        rows.add(List.of(productButton));

        // Amal tugmalarini alohida chiqarish
        rows.add(getProductActionsInlineKeyboard(product.getId()).getKeyboard().get(0));

        return rows;
    }

    public static InlineKeyboardMarkup getProductActionsInlineKeyboard(Long productId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton editButton = new InlineKeyboardButton();
        editButton.setText("✏ Edit");
        editButton.setCallbackData("EDIT_PRODUCT_" + productId);

        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        deleteButton.setText("🗑 Delete");
        deleteButton.setCallbackData("DELETE_PRODUCT_" + productId);

        rows.add(List.of(editButton, deleteButton));

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
