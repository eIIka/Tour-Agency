package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.ClientDTO;
import ua.ellka.touragency.model.Client;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(source = "user.email", target = "email")
    ClientDTO clientToClientDTO(Client client);

    @Mapping(source = "email", target = "user.email")
    Client clientDTOToClient(ClientDTO clientDTO);
}
