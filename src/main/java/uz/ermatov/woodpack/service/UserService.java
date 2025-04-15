package uz.ermatov.woodpack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.ermatov.woodpack.model.User;
import uz.ermatov.woodpack.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(Contact contact) {

        String phoneNumber = contact.getPhoneNumber();
        String firstName = contact.getFirstName();
        String lastName = contact.getLastName();
        Optional<User> optionalUser =
                userRepository.findByPhoneNumber(phoneNumber);
        User user = optionalUser.orElse(new User());
        user.setPhoneNumber(phoneNumber);
        user.setName(firstName + " " + lastName);
        userRepository.save(user);

    }
}
