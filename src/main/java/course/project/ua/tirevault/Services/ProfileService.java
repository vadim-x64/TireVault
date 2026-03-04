package course.project.ua.tirevault.Services;

import course.project.ua.tirevault.Entities.Models.Customer;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.ICustomerRepository;
import course.project.ua.tirevault.Repositories.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User updateProfile(Long userId, String firstName, String lastName, String middleName, String phone) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new Exception("Користувача не знайдено.");
        }

        User user = userOpt.get();
        Customer customer = user.getCustomer();

        if (!customer.getPhone().equals(phone)) {
            Optional<Customer> existingCustomerWithPhone = customerRepository.findByPhone(phone);
            if (existingCustomerWithPhone.isPresent() && !existingCustomerWithPhone.get().getId().equals(customer.getId())) {
                throw new Exception("Цей номер телефону вже використовується іншим користувачем.");
            }
        }

        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setMiddleName(middleName);
        customer.setPhone(phone);

        return userRepository.save(user);
    }

    @Transactional
    public User updateSecurity(Long userId, String username, String newPassword) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new Exception("Користувача не знайдено.");
        }

        User user = userOpt.get();

        if (!user.getUsername().equals(username)) {
            Optional<User> existingUser = userRepository.findByUsername(username);

            if (existingUser.isPresent()) {
                throw new Exception("Користувач з таким логіном вже існує.");
            }

            user.setUsername(username);
        }

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 8) {
                throw new Exception("Пароль має містити мінімум 8 символів.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId, String password) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new Exception("Користувача не знайдено.");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Невірний пароль! Акаунт не було видалено.");
        }

        userRepository.delete(user);
    }
}