package course.project.ua.tirevault.Controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "Виникла невідома помилка";
        int statusCode = 0;

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "Сторінку не знайдено. Можливо, ви ввели неправильну адресу.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Внутрішня помилка сервера. Ми вже працюємо над цим.";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "У вас немає доступу до цієї сторінки.";
            }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("page", "error");
        return "index";
    }
}