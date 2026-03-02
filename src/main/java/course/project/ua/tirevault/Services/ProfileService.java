package course.project.ua.tirevault.Services;

import course.project.ua.tirevault.Entities.Models.Customer;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.CustomerRepository;
import course.project.ua.tirevault.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User updateProfile(Long userId, String firstName, String lastName, String middleName, String phone) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new Exception("Користувача не знайдено!");
        }

        User user = userOpt.get();
        Customer customer = user.getCustomer();

        // Перевіряємо, чи змінився номер телефону і чи не зайнятий він іншим клієнтом
        if (!customer.getPhone().equals(phone)) {
            Optional<Customer> existingCustomerWithPhone = customerRepository.findByPhone(phone);
            if (existingCustomerWithPhone.isPresent() && !existingCustomerWithPhone.get().getId().equals(customer.getId())) {
                throw new Exception("Цей номер телефону вже використовується іншим користувачем!");
            }
        }

        // Оновлюємо дані
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setMiddleName(middleName);
        customer.setPhone(phone);

        // Зберігаємо зміни
        return userRepository.save(user);
    }

    @Transactional
    public User updateSecurity(Long userId, String username, String newPassword) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new Exception("Користувача не знайдено!");
        }

        User user = userOpt.get();

        // Перевіряємо, чи змінився логін
        if (!user.getUsername().equals(username)) {
            // Перевіряємо, чи не зайнятий новий логін кимось іншим
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                throw new Exception("Користувач з таким логіном вже існує!");
            }
            user.setUsername(username);
        }

        // Якщо користувач ввів новий пароль, хешуємо та зберігаємо його
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 8) {
                throw new Exception("Пароль має містити мінімум 8 символів!");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }
}
