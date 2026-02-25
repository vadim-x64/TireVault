package course.project.ua.tirevault.Services;

import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.Customer;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.CustomerRepository;
import course.project.ua.tirevault.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Метод реєстрації
    @Transactional
    public User register(String firstName, String lastName, String middleName,
                         String phone, String username, String password) throws Exception {

        // Перевірки на унікальність
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Користувач з таким логіном вже існує!");
        }
        if (customerRepository.findByPhone(phone).isPresent()) {
            throw new Exception("Цей номер телефону вже зареєстровано!");
        }

        // Створюємо клієнта
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setMiddleName(middleName);
        customer.setPhone(phone); // Зберігаємо у форматі +38(xxx)-xxx-xx-xx

        // Створюємо юзера
        User user = new User();
        user.setUsername(username);
        // Поки що зберігаємо пароль як є. Пізніше тут буде BCrypt хешування!
        user.setPassword(password);
        user.setRole(UserRole.USER);
        user.setCustomer(customer);

        // Оскільки стоїть CascadeType.ALL, достатньо зберегти лише User
        return userRepository.save(user);
    }

    // Метод авторизації
    public User login(String username, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new Exception("Невірний логін або пароль!");
        }

        User user = userOpt.get();
        // Перевіряємо пароль (поки без хешування)
        if (!user.getPassword().equals(password)) {
            throw new Exception("Невірний логін або пароль!");
        }

        return user;
    }
}