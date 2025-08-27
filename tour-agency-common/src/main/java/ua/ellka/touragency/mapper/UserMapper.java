package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);
}
