package uz.ermatov.woodpack.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.ermatov.woodpack.model.Admin;
import uz.ermatov.woodpack.repository.AdminRepository;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final AdminRepository adminRepository;

    @Value("${spring.sql.init.mode}")
    private String initMode;

    @Override
    public void run(String... args) throws Exception {
        if (initMode.equals("always")) {
            adminRepository.save(new Admin(
                    null,
                    "Samir",
                    423218877L,
                    "1234")
            );
        }
    }
}
