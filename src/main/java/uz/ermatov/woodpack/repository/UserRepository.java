package uz.ermatov.woodpack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.ermatov.woodpack.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByTelegramId(long telegramId);
}
