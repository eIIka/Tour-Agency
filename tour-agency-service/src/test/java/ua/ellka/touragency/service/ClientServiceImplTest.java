package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.ClientDTO;
import ua.ellka.touragency.mapper.ClientMapper;
import ua.ellka.touragency.model.*;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.ClientRepo;
import ua.ellka.touragency.repo.CountryRepo;
import ua.ellka.touragency.repo.UserRepo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientServiceImplTest {

    private ClientService clientService;
    private ClientRepo clientRepo;
    private BookingRepo bookingRepo;
    private CountryRepo countryRepo;
    private UserRepo userRepo;
    private Client client;
    private Client client2;
    private ClientDTO clientDTO;
    private User user;

    @BeforeEach
    void setUp() {
        ClientMapper clientMapper = ClientMapper.INSTANCE;
        clientRepo = mock(ClientRepo.class);
        bookingRepo = mock(BookingRepo.class);
        countryRepo = mock(CountryRepo.class);
        userRepo = mock(UserRepo.class);
        clientService = new ClientServiceImpl(clientMapper, clientRepo, bookingRepo, countryRepo, userRepo);

        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("test_password");
        user.setRole("ROLE_CLIENT");

        clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setName("test name vlad");
        clientDTO.setPhone("+380999999999");
        clientDTO.setPassportNumber("11111111111111");
        clientDTO.setEmail("test@gmail.com");

        client = new Client();
        client.setId(1L);
        client.setName("test name vlad");
        client.setPhone("+380999999999");
        client.setPassportNumber("11111111111111");
        client.setUser(user);

        client2 = new Client();
        client2.setId(2L);
        client2.setName("test name vlad");
        client2.setPhone("+380999999999");
        client2.setPassportNumber("11111111111111");
        client2.setUser(user);
    }

    @Test
    void createClientTest_success() {
        when(clientRepo.findByPassportNumber(any())).thenReturn(Optional.empty());
        when(clientRepo.findByPhone(any())).thenReturn(Optional.empty());
        when(clientRepo.findByName(any())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user));
        when(clientRepo.save(any())).thenReturn(client);

        ClientDTO created = clientService.createClient(clientDTO);

        assertNotNull(created);
        assertEquals(clientDTO.getId(), created.getId());
        assertEquals(clientDTO.getName(), created.getName());
        assertEquals(clientDTO.getPhone(), created.getPhone());
        assertEquals(clientDTO.getPassportNumber(), created.getPassportNumber());
        assertEquals(clientDTO.getEmail(), created.getEmail());
    }

    @Test
    void createClientTest_clientNameAlreadyExistsException() {
        when(clientRepo.findByName(any())).thenReturn(Optional.of(client));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> clientService.createClient(clientDTO));
        assertEquals("Name already exists", exception.getMessage());
    }

    @Test
    void createClientTest_clientPhoneAlreadyExistsException() {
        when(clientRepo.findByPhone(any())).thenReturn(Optional.of(client));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> clientService.createClient(clientDTO));
        assertEquals("Phone already exists", exception.getMessage());
    }

    @Test
    void createClientTest_clientPassportNumberAlreadyExistsException() {
        when(clientRepo.findByPassportNumber(any())).thenReturn(Optional.of(client));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> clientService.createClient(clientDTO));
        assertEquals("Passport number already exists", exception.getMessage());
    }

    @Test
    void createClientTest_clientEmailNotFoundException() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> clientService.createClient(clientDTO));
        assertEquals("User with email not found", exception.getMessage());
    }

    @Test
    void getAllClientsTest_success() {
        when(clientRepo.count()).thenReturn(1L);
        when(clientRepo.findAll()).thenReturn(List.of(client));

        List<ClientDTO> allClients = clientService.getAllClients();

        assertNotNull(allClients);
        ClientDTO clientDTOList = allClients.get(0);
        assertNotNull(clientDTOList);
        assertEquals(clientDTOList.getId(), clientDTO.getId());
        assertEquals(clientDTOList.getName(), clientDTO.getName());
        assertEquals(clientDTOList.getPhone(), clientDTO.getPhone());
        assertEquals(clientDTOList.getPassportNumber(), clientDTO.getPassportNumber());
    }

    @Test
    void getAllClients_clientNotFoundException() {
        when(clientRepo.count()).thenReturn(0L);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> clientService.getAllClients());
        assertEquals("Clients not found", exception.getMessage());
    }

    @Test
    void updateClientTest_success() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(clientRepo.findByPhone(any())).thenReturn(Optional.empty());
        when(clientRepo.findByPhone(any())).thenReturn(Optional.empty());
        when(clientRepo.findByName(any())).thenReturn(Optional.empty());
        when(clientRepo.save(any())).thenReturn(client);

        ClientDTO updated = clientService.updateClient(1L, clientDTO);

        assertNotNull(updated);
        assertEquals(clientDTO.getId(), updated.getId());
        assertEquals(clientDTO.getName(), updated.getName());
        assertEquals(clientDTO.getPhone(), updated.getPhone());
        assertEquals(clientDTO.getPassportNumber(), updated.getPassportNumber());
    }

    @Test
    void updateClientTest_clientNotFoundException() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> clientService.updateClient(1L, clientDTO));
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    void updateClientTest_clientNameAlreadyExistsException() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(clientRepo.findByName(any())).thenReturn(Optional.of(client2));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> clientService.updateClient(1L, clientDTO));
        assertEquals("Name already exists", exception.getMessage());
    }

    @Test
    void updateClientTest_clientPhoneAlreadyExistsException() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(clientRepo.findByPhone(any())).thenReturn(Optional.of(client2));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> clientService.updateClient(1L, clientDTO));
        assertEquals("Phone already exists", exception.getMessage());
    }

    @Test
    void updateClientTest_clientPassportNumberAlreadyExistsException() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(clientRepo.findByPassportNumber(any())).thenReturn(Optional.of(client2));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> clientService.updateClient(1L, clientDTO));
        assertEquals("Passport number already exists", exception.getMessage());
    }

    @Test
    void deleteClientTest_success() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));

        ClientDTO deleted = clientService.deleteClient(1L);

        assertNotNull(deleted);
        assertEquals(clientDTO.getId(), deleted.getId());
        assertEquals(clientDTO.getName(), deleted.getName());
        assertEquals(clientDTO.getPhone(), deleted.getPhone());
        assertEquals(clientDTO.getPassportNumber(), deleted.getPassportNumber());
    }

    @Test
    void deleteClientTest_clientNotFoundException() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> clientService.deleteClient(1L));
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    void getClientsByCountryTest_success() {
        Country country = new Country();
        country.setId(1L);
        country.setName("Ukraine");
        country.setRegion("Odessa");

        Tour tour = new Tour();
        tour.setId(1L);
        tour.setCountry(country);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setClient(client);
        booking.setTour(tour);

        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(bookingRepo.findBookingsByTourCountryId(anyLong())).thenReturn(List.of(booking));

        List<ClientDTO> clientsByCountry = clientService.getClientsByCountry(country.getId());

        assertNotNull(clientsByCountry);
        ClientDTO clientDTOList = clientsByCountry.get(0);
        assertNotNull(clientDTOList);
        assertEquals(clientDTOList.getId(), clientDTO.getId());
        assertEquals(clientDTOList.getName(), clientDTO.getName());
        assertEquals(clientDTOList.getPhone(), clientDTO.getPhone());
        assertEquals(clientDTOList.getPassportNumber(), clientDTO.getPassportNumber());
    }

    @Test
    void getClientsByCountryTest_countryNotFoundException() {
        when(countryRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> clientService.getClientsByCountry(1L));
        assertEquals("Country not found", exception.getMessage());
    }

    @Test
    void getClientsByCountryTest_clientsNotFoundException() {
        Country country = new Country();
        country.setId(1L);
        country.setName("Ukraine");
        country.setRegion("Odessa");

        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(bookingRepo.findBookingsByTourCountryId(anyLong())).thenReturn(List.of());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> clientService.getClientsByCountry(1L));
        assertEquals("No clients found who visited the country with id 1", exception.getMessage());
    }
}