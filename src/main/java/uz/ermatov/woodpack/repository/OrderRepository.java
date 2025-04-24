package uz.ermatov.woodpack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.ermatov.woodpack.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByTelegramIdAndConfirmFalse(long telegramId);

    List<Order> findAllByConfirmTrueAndAcceptanceFalse();
}
