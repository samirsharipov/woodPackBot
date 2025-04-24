package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.ermatov.woodpack.buttons.InlineKeyboardUtils;
import uz.ermatov.woodpack.repository.AdminRepository;
import uz.ermatov.woodpack.telegram.Messages;
import uz.ermatov.woodpack.telegram.TelegramBotController;

@Component
@RequiredArgsConstructor
public class HandleCallbackQueryService {
    private final ProductService productService;
    private final TelegramBotController botController;
    private final UserStateService userStateService;
    private final InlineKeyboardUtils inlineKeyboardUtils;
    private final AdminService adminService;
    private final OrderService orderService;
    private final Messages messages;

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        long telegramId = callbackQuery.getFrom().getId();

        botController.deleteMessage(chatId, messageId);
        if (data.startsWith("PRODUCT_")) {
            Long productId = Long.parseLong(data.replace("PRODUCT_", ""));
            productService.getProductById(chatId, productId, !adminService.isAdmin(chatId));
        } else if (data.startsWith("EDIT_PRODUCT_")) {
            long productId = Long.parseLong(data.replace("EDIT_PRODUCT_", ""));
            botController.sendMessage(chatId, "Mahsulot nomini kiriting:");
            userStateService.saveState(chatId, "UPDATE_PRODUCT");
            userStateService.saveTempData(chatId, "PRODUCT_ID", Long.toString(productId));
        } else if (data.startsWith("DELETE_PRODUCT_")) {
            long productId = Long.parseLong(data.replace("DELETE_PRODUCT_", ""));
            botController.sendMessage(chatId, "⛔Ushbu mahsulot o'chirilsinmi:", inlineKeyboardUtils.yesOrNo(productId, "PRODUCT"));
            userStateService.saveState(chatId, "DELETE_PRODUCT");
        } else if (data.startsWith("DELETE_CONFIRM_PRODUCT")) {
            long productId = Long.parseLong(data.replace("DELETE_CONFIRM_PRODUCT", ""));
            userStateService.saveState(chatId, "START");
            productService.delete(productId, chatId);
        } else if (data.startsWith("DELETE_REJECT_PRODUCT")) {
            long productId = Long.parseLong(data.replace("DELETE_REJECT_PRODUCT", ""));
            userStateService.saveState(chatId, "START");
            productService.rejectDelete(productId, chatId);
        } else if (data.startsWith("NEXT_PAGE_")) {
            int currentPage = userStateService.getPage(chatId);
            int nextPage = currentPage + 1;
            userStateService.savePage(chatId, nextPage); // ✅ Sahifani saqlash
            sendProductsList(chatId, nextPage); // ✅ Keyingi sahifadagi mahsulotlarni yuborish
        } else if (data.startsWith("PREV_PAGE_")) {
            int currentPage = userStateService.getPage(chatId);
            int prevPage = Math.max(currentPage - 1, 0);
            userStateService.savePage(chatId, prevPage); // ✅ Sahifani yangilash
            sendProductsList(chatId, prevPage); // ✅ Oldingi sahifadagi mahsulotlarni yuborish
        } else if (data.startsWith("PREVIEW_PRODUCT_")) {
            sendProductsList(chatId, userStateService.getPage(chatId));
        } else if (data.startsWith("ADMIN_")) {
            Long adminId = Long.parseLong(data.replace("ADMIN_", ""));
            adminService.getById(chatId, adminId);
        } else if (data.startsWith("DELETE_ADMIN_")) {
            long productId = Long.parseLong(data.replace("DELETE_ADMIN_", ""));
            botController.sendMessage(chatId, "⛔Ushbu mahsulot o'chirilsinmi:", inlineKeyboardUtils.yesOrNo(productId, "ADMIN"));
            userStateService.saveState(chatId, "DELETE_ADMIN");
        } else if (data.startsWith("DELETE_CONFIRM_ADMIN")) {
            long adminId = Long.parseLong(data.replace("DELETE_CONFIRM_ADMIN", ""));
            userStateService.saveState(chatId, "START");
            adminService.delete(chatId, adminId);
        } else if (data.startsWith("DELETE_REJECT_ADMIN")) {
            long adminId = Long.parseLong(data.replace("DELETE_REJECT_ADMIN", ""));
            userStateService.saveState(chatId, "START");
            adminService.rejectDelete(chatId, adminId);
        } else if (data.startsWith("ORDER_")) {
            long productId = Long.parseLong(data.replace("ORDER_", ""));
            orderService.save(productId, telegramId);
            botController.sendMessage(chatId, messages.getMessage(chatId, "added_order"));
            sendProductsList(chatId, userStateService.getPage(chatId));
        } else if (data.startsWith("CONFIRM_ORDER_")) {
            long productId = Long.parseLong(data.replace("CONFIRM_ORDER_", ""));
            orderService.updateOrder(productId, chatId);
        } else if (data.startsWith("ACCEPT_ORDER_")) {
            long orderId = Long.parseLong(data.replace("ACCEPT_ORDER_", ""));
            orderService.accept(orderId);
            orderService.getAllOrderForAdmin(chatId);
        }
    }

    private void sendProductsList(long chatId, int page) {
        productService.getAllProducts(chatId, page, false);
    }
}
