package course.project.ua.tirevault.Configuration;

import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.Customer;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.ICustomerRepository;
import course.project.ua.tirevault.Repositories.IUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final IUserRepository userRepository;
    private final ICustomerRepository customerRepository;

    public OAuth2LoginSuccessHandler(IUserRepository userRepository,
                                     ICustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Дані від Google
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        if (lastName == null) lastName = "";

        // Шукаємо існуючого юзера за username = email
        Optional<User> existingUser = userRepository.findByUsername(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // Створюємо нового
            Customer customer = new Customer();
            customer.setFirstName(firstName != null ? firstName : "Google");
            customer.setLastName(lastName);
            customer.setMiddleName(null);
            // Телефон порожній — юзер заповнить у профілі
            // Унікальний placeholder, щоб не порушити unique constraint
            customer.setPhone("" + email.hashCode());

            User newUser = new User();
            newUser.setUsername(email);
            // Пароль — заглушка (BCrypt від випадкового UUID), логін паролем неможливий
            newUser.setPassword("OAUTH2_GOOGLE_NO_PASSWORD");
            newUser.setRole(UserRole.USER);
            newUser.setCustomer(customer);
            user = userRepository.save(newUser);
        }

        if (user.isBlocked()) {
            response.sendRedirect("/auth?blocked=true");
            return;
        }

        // Кладемо в сесію — так само як твій AuthController
        HttpSession session = request.getSession(true);
        session.setAttribute("loggedUser", user);

        response.sendRedirect("/");
    }
}