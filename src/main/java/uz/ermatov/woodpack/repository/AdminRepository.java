package uz.ermatov.woodpack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.ermatov.woodpack.model.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByTelegramId(Long telegramId);
}
