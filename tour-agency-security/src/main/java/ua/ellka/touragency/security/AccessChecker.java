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
        return clientRepo.findById(clientId)
                .map(client -> client.getUser().getId().equals(getCurrentUserId()))
                .orElse(false);
    }

    public boolean isGuideOwner(Long guideId) {
        return guideRepo.findById(guideId)
                .map(client -> client.getUser().getId().equals(getCurrentUserId()))
                .orElse(false);
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

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof TourAgencyUserDetails userDetails) {
            return userDetails.getId();
        }
        return null;
    }
}
