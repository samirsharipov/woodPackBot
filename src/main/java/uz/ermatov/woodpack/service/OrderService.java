package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ermatov.woodpack.model.Order;
import uz.ermatov.woodpack.model.User;
import uz.ermatov.woodpack.repository.OrderRepository;
import uz.ermatov.woodpack.repository.UserRepository;
import uz.ermatov.woodpack.repository.ProductRepository;
import uz.ermatov.woodpack.telegram.Messages;
import uz.ermatov.woodpack.telegram.TelegramBotController;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TelegramBotController botController;
    private final Messages messages;

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
                botController.sendMessage(chatId, messages.getMessage(chatId,"not_found_order"));
            } else {
                StringBuilder message = new StringBuilder("Siz tanlagan mahsulotlar:\n");
                for (Long id : order.getProductIdList()) {
                    productRepository.findById(id).ifPresent(product -> {
                        message.append("• ").append(product.getName())
                               .append(" - ").append(product.getPrice()).append(" so'm\n");
                    });
                }
                botController.sendMessage(chatId, message.toString());
            }
        } else {
            botController.sendMessage(chatId, "Sizda hali hech qanday aktiv buyurtma mavjud emas.");
        }
    }
}
