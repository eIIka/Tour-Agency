package ua.ellka.touragency.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.ellka.touragency.model.security.TourAgencyUserDetails;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.ClientRepo;
import ua.ellka.touragency.repo.GuideRepo;
import ua.ellka.touragency.repo.TourRepo;

@Component("accessChecker")
@RequiredArgsConstructor
public class AccessChecker {
    private final ClientRepo clientRepo;
    private final GuideRepo guideRepo;
    private final BookingRepo bookingRepo;
    private final TourRepo tourRepo;

    public boolean isClientOwner(Long clientId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) return false;

        // Шукаємо клієнта, ID якого передано, і перевіряємо, чи збігається його User ID з поточним
        return clientRepo.findById(clientId)
                .map(client -> client.getUser().getId().equals(currentUserId))
                .orElse(false);
    }

    // Аналогічно для гіда
    public boolean isGuideOwner(Long userId) {
        Long currentUserId = getCurrentUserId();

        if (currentUserId == null || !currentUserId.equals(userId)) {
            return false;
        }
        return guideRepo.findByUserId(userId).isPresent();
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof TourAgencyUserDetails userDetails) {
            return userDetails.getId();
        }
        return null;
    }

    public boolean isBookingOwner(Long bookingId) {
        return bookingRepo.findById(bookingId)
                .map(booking -> booking.getClient().getUser().getId().equals(getCurrentUserId()))
                .orElse(false);

    }

    public boolean isTourOwner(Long tourId) {
        return tourRepo.findById(tourId)
                .map(tour -> tour.getGuide().getUser().getId().equals(getCurrentUserId()))
                .orElse(false);
    }
}
