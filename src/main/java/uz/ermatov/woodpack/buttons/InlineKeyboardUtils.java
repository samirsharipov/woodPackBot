package uz.ermatov.woodpack.buttons;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.ermatov.woodpack.model.Admin;
import uz.ermatov.woodpack.model.Product;
import uz.ermatov.woodpack.telegram.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InlineKeyboardUtils {

    private final Messages messages;

    public InlineKeyboardMarkup getProductListInlineKeyboard(List<Product> products, int page, boolean hasNext, boolean hasPrevious, boolean isForUser, long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Product product : products) {
            InlineKeyboardButton productButton = new InlineKeyboardButton();
            productButton.setText("📦 " + product.getName());
            productButton.setCallbackData("PRODUCT_" + product.getId());

            rows.add(List.of(productButton));
        }

        // Navigatsiya tugmalari
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();

        if (hasPrevious) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton();
            prevButton.setText("⬅ Oldingi");
            prevButton.setCallbackData("PREV_PAGE_" + (page - 1));
            navigationRow.add(prevButton);
        }

        if (hasNext) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton();
            nextButton.setText("Keyingi ➡");
            nextButton.setCallbackData("NEXT_PAGE_" + (page + 1));
            navigationRow.add(nextButton);
        }

        if (!navigationRow.isEmpty()) {
            rows.add(navigationRow);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getProductActionsInlineKeyboard(Long productId, long chatId, boolean isForUser) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();


        if (isForUser) {

            InlineKeyboardButton ordersButton = new InlineKeyboardButton();
            ordersButton.setText(messages.getMessage(chatId, "order"));
            ordersButton.setCallbackData("ORDER_" + productId);


            InlineKeyboardButton prevButton = new InlineKeyboardButton();
            prevButton.setText("⬅ Orqaga");
            prevButton.setCallbackData("PREVIEW_PRODUCT_");

            rows.add(List.of(ordersButton, prevButton));
        } else {

            InlineKeyboardButton editButton = new InlineKeyboardButton();
            editButton.setText("✏ Edit");
            editButton.setCallbackData("EDIT_PRODUCT_" + productId);

            InlineKeyboardButton prevButton = new InlineKeyboardButton();
            prevButton.setText("⬅ Orqaga");
            prevButton.setCallbackData("PREVIEW_PRODUCT_");

            InlineKeyboardButton deleteButton = new InlineKeyboardButton();
            deleteButton.setText("🗑 Delete");
            deleteButton.setCallbackData("DELETE_PRODUCT_" + productId);
            rows.add(List.of(editButton, prevButton, deleteButton));
        }


        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getAdminActionsInlineKeyboard(Long productId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();


        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("⬅ Orqaga");
        prevButton.setCallbackData("PREVIEW_ADMIN_");

        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        deleteButton.setText("🗑 Delete");
        deleteButton.setCallbackData("DELETE_ADMIN_" + productId);

        rows.add(List.of(prevButton, deleteButton));

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup yesOrNo(long productId, String customName) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("✅ Xa");
        yesButton.setCallbackData("DELETE_CONFIRM_" + customName + productId);

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("❌ Yo'q");
        noButton.setCallbackData("DELETE_REJECT_" + customName + productId);

        rows.add(Arrays.asList(yesButton, noButton));
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup getProductListKeyboard(int page, List<Product> allProducts, boolean isForUser, long chatId) {
        int pageSize = 5; // Har bir sahifada nechta mahsulot bo‘lishini belgilash
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allProducts.size());

        if (startIndex >= allProducts.size()) {
            startIndex = Math.max(0, allProducts.size() - pageSize);
            endIndex = allProducts.size();
        }

        List<Product> productsOnPage = allProducts.subList(startIndex, endIndex);
        boolean hasNext = endIndex < allProducts.size();
        boolean hasPrevious = startIndex > 0;

        return getProductListInlineKeyboard(productsOnPage, page, hasNext, hasPrevious, isForUser, chatId);
    }


    public ReplyKeyboard getAdminListKeyboard(List<Admin> all) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Admin admin : all) {
            InlineKeyboardButton adminButton = new InlineKeyboardButton();
            adminButton.setText("👤 " + admin.getName());
            adminButton.setCallbackData("ADMIN_" + admin.getId());
            rows.add(List.of(adminButton));
        }
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup orderConfirm(long chatId, long orderId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText(messages.getMessage(chatId, "button_confirm"));
        confirmButton.setCallbackData("CONFIRM_ORDER_" + orderId);
        markup.setKeyboard(List.of(List.of(confirmButton)));
        return markup;
    }

    public InlineKeyboardMarkup orderAccept(long chatId, Long id) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton acceptButton = new InlineKeyboardButton();
        acceptButton.setText("✅ Qabul qilish");
        acceptButton.setCallbackData("ACCEPT_ORDER_" + id);
        markup.setKeyboard(List.of(List.of(acceptButton)));
        return markup;
    }
}