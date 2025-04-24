package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ermatov.woodpack.buttons.InlineKeyboardUtils;
import uz.ermatov.woodpack.model.Order;
import uz.ermatov.woodpack.model.Product;
import uz.ermatov.woodpack.model.User;
import uz.ermatov.woodpack.repository.OrderRepository;
import uz.ermatov.woodpack.repository.UserRepository;
import uz.ermatov.woodpack.repository.ProductRepository;
import uz.ermatov.woodpack.telegram.Messages;
import uz.ermatov.woodpack.telegram.TelegramBotController;

import java.util.HashSet;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TelegramBotController botController;
    private final Messages messages;
    private final InlineKeyboardUtils inlineKeyboardUtils;

    public void save(long productId, long telegramId) {

        Order order = orderRepository.findByTelegramIdAndConfirmFalse(telegramId)
                .orElseGet(() -> {
                    User user = userRepository.findByTelegramId(telegramId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + telegramId));

                    Order newOrder = new Order();
                    newOrder.setUserId(user.getId());
                    newOrder.setTelegramId(telegramId);
                    newOrder.setAcceptance(false);
                    newOrder.setProductIdList(new HashSet<>());
                    return newOrder;
                });

        order.getProductIdList().add(productId);
        orderRepository.save(order);
    }

    public void getAllByTelegramId(long telegramId, long chatId) {
        Optional<Order> optionalOrder = orderRepository.findByTelegramIdAndConfirmFalse(telegramId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (order.getProductIdList().isEmpty()) {
                botController.sendMessage(chatId, messages.getMessage(chatId, "not_found_order"));
            } else {
                StringBuilder message = new StringBuilder(messages.getMessage(chatId, "selected_product") + "\n");
                double total = 0;
                for (Long id : order.getProductIdList()) {
                    productRepository.findById(id).ifPresent(product -> {
                        message.append("• ").append(product.getName())
                                .append(" - ").append(product.getPrice()).append(" so'm\n");
                    });
                    total += productRepository.findById(id).map(Product::getPrice).orElse(0.0);
                }
                message.append("\n")
                        .append(messages.getMessage(chatId, "total"))
                        .append(total).append(" ")
                        .append(messages.getMessage(chatId, "sum"));


                botController.sendMessage(chatId, message.toString(), inlineKeyboardUtils.orderConfirm(chatId, order.getId()));
            }
        } else {
            botController.sendMessage(chatId, messages.getMessage(chatId, "order_empty"));
        }
    }

    public void updateOrder(long productId, long chatId) {
        orderRepository.findById(productId).ifPresent(order -> {
            order.setConfirm(true);
            orderRepository.save(order);
        });
        botController.sendMessage(chatId, messages.getMessage(chatId, "order_updated"));
    }

    public void getAllOrderForAdmin(long chatId) {
        List<Order> orders = orderRepository.findAllByConfirmTrueAndAcceptanceFalse();

        if (orders.isEmpty()) {
            botController.sendMessage(chatId, "Hech qanday tasdiqlangan buyurtma topilmadi.");
        } else {
            for (Order order : orders) {
                Optional<User> userOpt = userRepository.findById(order.getUserId());
                String userInfo = userOpt.map(user ->
                        "👤 Ism: " + user.getName() + "\n📞 Tel: " + user.getPhoneNumber()
                ).orElse("👤 Foydalanuvchi topilmadi");

                StringBuilder message = new StringBuilder();
                message.append("📦 Order ID: ").append(order.getId()).append("\n")
                        .append(userInfo).append("\n")
                        .append("🛒 Mahsulotlar:\n");

                for (Long productId : order.getProductIdList()) {
                    productRepository.findById(productId).ifPresent(product ->
                            message.append("• ").append(product.getName()).append("\n")
                    );
                }

                botController.sendMessage(chatId, message.toString(), inlineKeyboardUtils.orderAccept(chatId, order.getId()));
            }
        }
    }

    public void accept(long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setConfirm(true);
            order.setAcceptance(true);
            orderRepository.save(order);
        });
        botController.sendMessage(orderId, "Qabul qilindi!");
    }
}
