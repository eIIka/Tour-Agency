package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.GuideDTO;
import ua.ellka.touragency.model.Guide;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface GuideMapper {
    GuideMapper INSTANCE = Mappers.getMapper(GuideMapper.class);

    @Mapping(source = "user.email", target = "email")
    GuideDTO guideToGuideDTO(Guide guide);

    @Mapping(source = "email", target = "user.email")
    Guide guideDTOToGuide(GuideDTO guideDTO);
}
