package ua.ellka.touragency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.ellka.touragency.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByClientId(Long clientId);
    List<Booking> findBookingsByTourId(Long tourId);
    List<Booking> findBookingsByTourCountryId(Long countryId);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.tour.id = :tourId " +
           "AND b.client.id = :clientId " +
           "AND b.bookingDate = :bookingDate")
    Optional<Booking> findExistingBooking(
            @Param("tourId") Long tourId,
            @Param("clientId") Long clientId,
            @Param("bookingDate") LocalDate bookingDate
    );

    @Query("SELECT TO_CHAR(b.bookingDate, 'Month') AS month, COUNT(b) AS count FROM Booking b GROUP BY TO_CHAR(b.bookingDate, 'Month')")
    List<BookingCountByMonthResult> findBookingCountsByMonth();

    interface BookingCountByMonthResult {
        String getMonth();
        Long getCount();
    }

    @Query("SELECT b.tour.id AS tourId, COUNT(b) AS bookingCount FROM Booking b GROUP BY b.tour.id")
    List<TourBookingCountResult> findBookingCountsByTour();

    interface TourBookingCountResult {
        Long getTourId();
        Long getBookingCount();
    }
}
