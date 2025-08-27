package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ua.ellka.touragency.dto.ClientDTO;
import ua.ellka.touragency.model.Client;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ClientMapper {
    @Mapping(source = "user.email", target = "email")
    ClientDTO clientToClientDTO(Client client);

    @Mapping(source = "email", target = "user.email")
    Client clientDTOToClient(ClientDTO clientDTO);
}
