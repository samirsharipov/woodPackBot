package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ermatov.woodpack.buttons.InlineKeyboardUtils;
import uz.ermatov.woodpack.model.Admin;
import uz.ermatov.woodpack.model.Product;
import uz.ermatov.woodpack.repository.AdminRepository;
import uz.ermatov.woodpack.repository.ProductRepository;
import uz.ermatov.woodpack.telegram.TelegramBotController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final ProductRepository productRepository;
    private final TelegramBotController botController;
    private final InlineKeyboardUtils keyboardUtils;
    private final UserStateService userStateService;

    public boolean isAdmin(Long telegramId) {
        return adminRepository.findByTelegramId(telegramId).isPresent();
    }

    public boolean checkPassword(Long telegramId, String password) {
        Optional<Admin> admin = adminRepository.findByTelegramId(telegramId);
        return admin.isPresent() && admin.get().getPassword().equals(password);
    }

    public void addAdmin(Long chatId) {

        String userTelegramId = userStateService.getTempData(chatId, "ADD_ADMIN_ID");
        String name = userStateService.getTempData(chatId, "ADD_ADMIN_NAME");

        long userId = Long.parseLong(userTelegramId);
        if (adminRepository.existsById(userId)) {
            botController.sendMessage(chatId, "Ushbu id lik admin allaqachon mavjud!");
        }

        Admin admin = new Admin();
        admin.setName(name);
        admin.setTelegramId(userId);
        adminRepository.save(admin);

        String adminInfo = "✅ Admin saqlandi! \n" +
                "👤Name : " + admin.getName() + " \n" +
                "🆔Telegram id : " + admin.getTelegramId();

        botController.sendMessage(chatId, adminInfo);
        userStateService.saveState(chatId, "START");
    }

    public void removeAdmin(Long telegramId) {
        adminRepository.findByTelegramId(telegramId)
                .ifPresent(adminRepository::delete);
    }

    public void getAllAdmins(long chatId) {
        List<Admin> all = adminRepository.findAll();
        botController.sendMessage(chatId, "Adminlar ro'yxati : ", keyboardUtils.getAdminListKeyboard(all));
    }

    public void getById(long chatId, Long adminId) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            String adminInfo = "👤Name : " + admin.getName() + " \n" +
                    "🆔Telegram id : " + admin.getTelegramId();
            botController.sendMessage(chatId, adminInfo, InlineKeyboardUtils.getAdminActionsInlineKeyboard(adminId));
        } else {
            botController.sendMessage(chatId, "Not exist admin");
        }
    }

    public void delete(long chatId, long adminId) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        if (optionalAdmin.isEmpty()) {
            botController.sendMessage(chatId, "Admin allaqachon o‘chirilgan!");
        } else {
            adminRepository.deleteById(adminId);
            botController.sendMessage(chatId, "Admin o‘chirildi ✅");
        }
    }

    public void rejectDelete(long chatId, long adminId) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        if (optionalAdmin.isEmpty()) {
            botController.sendMessage(chatId, "Admin allaqachon o‘chirilgan!");
        } else {
            botController.sendMessage(chatId, "Bekor qilindi ❌");
        }
    }
}
