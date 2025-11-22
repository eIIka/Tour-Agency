package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.TourDTO;
import ua.ellka.touragency.model.Tour;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface TourMapper {
    TourMapper INSTANCE = Mappers.getMapper(TourMapper.class);

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "guide.id", target = "guideId")
    @Mapping(source = "country.name", target = "countryName")
    @Mapping(source = "guide.name", target = "guideName")
    @Mapping(source = "country.region", target = "countryRegion")
    TourDTO tourToTourDTO(Tour tour);

    // Мапінг з TourDTO на Tour (Вхідні дані) - ЛИШЕ ПРОСТІ ПОЛЯ
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "guide", ignore = true)
    Tour tourDTOToTour(TourDTO tourDTO);
}
