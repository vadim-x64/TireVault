package course.project.ua.tirevault.Services;

import course.project.ua.tirevault.Entities.Models.Customer;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.CustomerRepository;
import course.project.ua.tirevault.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

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

        // Зберігаємо зміни (завдяки CascadeType.ALL в сутності User, customer теж оновиться)
        return userRepository.save(user);
    }
}
