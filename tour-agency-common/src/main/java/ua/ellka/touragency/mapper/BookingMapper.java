package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.model.Booking;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "tour.id", target = "tourId")
    BookingDTO bookingToBookingDTO(Booking booking);

    @Mapping(source = "clientId", target = "client.id")
    @Mapping(source = "tourId", target = "tour.id")
    Booking bookingDTOToBooking(BookingDTO bookingDTO);
}
