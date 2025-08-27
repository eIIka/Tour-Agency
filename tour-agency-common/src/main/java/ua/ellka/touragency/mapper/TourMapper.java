package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ua.ellka.touragency.dto.TourDTO;
import ua.ellka.touragency.model.Tour;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface TourMapper {
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "guide.id", target = "guideId")
    TourDTO tourToTourDTO(Tour tour);

    @Mapping(source = "countryId", target = "country.id")
    @Mapping(source = "guideId", target = "guide.id")
    Tour tourDTOToTour(TourDTO tourDTO);
}
