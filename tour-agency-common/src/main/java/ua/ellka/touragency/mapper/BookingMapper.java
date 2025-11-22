package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.model.Booking;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "client", ignore = true)
    Booking bookingDTOToBooking(BookingDTO bookingDTO);

    // Мапінг Model -> DTO (Вихідні дані)
    // Витягуємо ID та об'єкт Tour
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "tour", target = "tour") // Передаємо повний об'єкт Tour (який мапер потім перетворить у TourDTO)
    BookingDTO bookingToBookingDTO(Booking booking);
}
