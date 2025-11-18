package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.ClientDTO;
import ua.ellka.touragency.model.Client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientMapperTest {

    @Test
    void clientToClientDTO() {
        ClientDTO clientDTO = ClientMapper.INSTANCE.clientToClientDTO(TestData.CLIENT);

        assertNotNull(clientDTO);
        assertEquals(TestData.CLIENT.getId(), clientDTO.getId());
        assertEquals(TestData.CLIENT.getName(), clientDTO.getName());
        assertEquals(TestData.CLIENT.getPassportNumber(), clientDTO.getPassportNumber());
        assertEquals(TestData.CLIENT.getPhone(), clientDTO.getPhone());
        assertEquals(TestData.CLIENT.getUser().getEmail(), clientDTO.getEmail());
    }

    @Test
    void clientDTOToClient() {
        Client client = ClientMapper.INSTANCE.clientDTOToClient(TestData.CLIENT_DTO);

        assertNotNull(client);
        assertEquals(TestData.CLIENT_DTO.getId(), client.getId());
        assertEquals(TestData.CLIENT_DTO.getName(), client.getName());
        assertEquals(TestData.CLIENT_DTO.getPassportNumber(), client.getPassportNumber());
        assertEquals(TestData.CLIENT_DTO.getPhone(), client.getPhone());
        assertEquals(TestData.CLIENT_DTO.getEmail(), client.getUser().getEmail());
    }
}
