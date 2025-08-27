package ua.ellka.touragency.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.ellka.touragency.dto.ClientDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.service.ClientService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ClientControllerTest extends ControllerParentTest {
    @Mock
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        ClientController clientController = new ClientController(clientService);

        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void getAllClientsTest_returnsOk() throws Exception {
        when(clientService.getAllClients()).thenReturn(List.of(new ClientDTO()));

        mockMvc.perform(get("/v1/client"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllClientsTest_returnsNotFound() throws Exception {
        when(clientService.getAllClients()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/client"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getClientsByCountryTest_returnsOk() throws Exception {
        when(clientService.getClientsByCountry(anyLong())).thenReturn(List.of(new ClientDTO()));

        mockMvc.perform(get("/v1/client/country/{countryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getClientsByCountryTest_returnsNotFound() throws Exception {
        when(clientService.getClientsByCountry(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/client/country/{countryId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createClientTest_returnsOk() throws Exception {
        when(clientService.createClient(any())).thenReturn(new ClientDTO());

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Test name");
        clientDTO.setPhone("+380999999999");
        clientDTO.setPassportNumber("AA123456");

        String reqBody = objectMapper.writeValueAsString(clientDTO);
        mockMvc.perform(
                        post("/v1/client")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createClientTest_returnsConflict() throws Exception {
        when(clientService.createClient(any())).thenThrow(ExistingServiceException.class);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Test name");
        clientDTO.setPhone("+380999999999");
        clientDTO.setPassportNumber("AA123456");

        String reqBody = objectMapper.writeValueAsString(clientDTO);
        mockMvc.perform(
                        post("/v1/client")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateClientTest_returnsOk() throws Exception {
        when(clientService.updateClient(anyLong(), any())).thenReturn(new ClientDTO());

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Test name");
        clientDTO.setPhone("+380999999999");
        clientDTO.setPassportNumber("AA123456");

        String reqBody = objectMapper.writeValueAsString(clientDTO);
        mockMvc.perform(
                        put("/v1/client/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateClientTest_returnsNotFound() throws Exception {
        when(clientService.updateClient(anyLong(), any())).thenThrow(NotFoundServiceException.class);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Test name");
        clientDTO.setPhone("+380999999999");
        clientDTO.setPassportNumber("AA123456");

        String reqBody = objectMapper.writeValueAsString(clientDTO);
        mockMvc.perform(
                        put("/v1/client/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateClientTest_returnsConflict() throws Exception {
        when(clientService.updateClient(anyLong(), any())).thenThrow(ExistingServiceException.class);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Test name");
        clientDTO.setPhone("+380999999999");
        clientDTO.setPassportNumber("AA123456");

        String reqBody = objectMapper.writeValueAsString(clientDTO);
        mockMvc.perform(
                        put("/v1/client/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteClientTest_returnsOk() throws Exception {
        when(clientService.deleteClient(anyLong())).thenReturn(new ClientDTO());

        mockMvc.perform(delete("/v1/client/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteClientTest_returnsNotFound() throws Exception {
        when(clientService.deleteClient(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(delete("/v1/client/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteClientTest_returnsConflict() throws Exception {
        when(clientService.deleteClient(anyLong())).thenThrow(ExistingServiceException.class);

        mockMvc.perform(delete("/v1/client/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
