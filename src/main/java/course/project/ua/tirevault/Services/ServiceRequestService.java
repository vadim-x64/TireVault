package course.project.ua.tirevault.Services;

import course.project.ua.tirevault.Entities.Enums.ServiceRequestStatus;
import course.project.ua.tirevault.Entities.Models.ServiceRequest;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.IServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRequestService {
    @Autowired
    private IServiceRequestRepository serviceRequestRepository;

    public ServiceRequest create(String customerName, String phone, String city, String description, User user) {
        ServiceRequest request = new ServiceRequest();
        request.setCustomerName(customerName);
        request.setPhone(phone);
        request.setCity(city);
        request.setDescription(description);
        request.setUser(user);
        request.setStatus(ServiceRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return serviceRequestRepository.save(request);
    }

    public List<ServiceRequest> getActiveByUser(User user) {
        return serviceRequestRepository.findByUserAndStatusInOrderByCreatedAtDesc(
                user, List.of(ServiceRequestStatus.PENDING, ServiceRequestStatus.ACCEPTED));
    }

    public List<ServiceRequest> getCompletedByUser(User user) {
        return serviceRequestRepository.findByUserAndStatusOrderByCreatedAtDesc(user, ServiceRequestStatus.COMPLETED);
    }

    public List<ServiceRequest> getAllActive() {
        return serviceRequestRepository.findByStatusInOrderByCreatedAtDesc(
                List.of(ServiceRequestStatus.PENDING, ServiceRequestStatus.ACCEPTED));
    }

    public List<ServiceRequest> getAllCompleted() {
        return serviceRequestRepository.findByStatusOrderByCreatedAtDesc(ServiceRequestStatus.COMPLETED);
    }

    public void accept(Long id) {
        ServiceRequest request = serviceRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Замовлення не знайдено."));
        request.setStatus(ServiceRequestStatus.ACCEPTED);
        request.setSeen(false);
        serviceRequestRepository.save(request);
    }

    public void complete(Long id) {
        ServiceRequest request = serviceRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Замовлення не знайдено."));
        request.setStatus(ServiceRequestStatus.COMPLETED);
        request.setSeen(false);
        serviceRequestRepository.save(request);
    }

    public long countPending() {
        return serviceRequestRepository.countByStatus(ServiceRequestStatus.PENDING);
    }

    public long countUnseenByUser(User user) {
        return serviceRequestRepository.countByUserAndSeenFalse(user);
    }

    @jakarta.transaction.Transactional
    public void markAllSeenByUser(User user) {
        List<ServiceRequest> unseen = serviceRequestRepository.findByUserAndSeenFalse(user);
        unseen.forEach(r -> r.setSeen(true));
        serviceRequestRepository.saveAll(unseen);
    }
}