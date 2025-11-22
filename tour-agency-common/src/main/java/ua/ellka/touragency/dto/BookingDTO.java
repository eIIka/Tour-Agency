package ua.ellka.touragency.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private Long tourId;
    private TourDTO tour;
    private Long clientId;
    private LocalDate bookingDate;
}
