package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ua.ellka.touragency.dto.ClientDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.mapper.ClientMapper;
import ua.ellka.touragency.model.Booking;
import ua.ellka.touragency.model.Client;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.ClientRepo;
import ua.ellka.touragency.repo.CountryRepo;
import ua.ellka.touragency.repo.UserRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientMapper clientMapper;
    private final ClientRepo clientRepo;
    private final BookingRepo bookingRepo;
    private final CountryRepo countryRepo;
    private final UserRepo userRepo;

    //8
    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = clientMapper.clientDTOToClient(clientDTO);

        clientRepo.findByPassportNumber(client.getPassportNumber())
                .ifPresent(c -> {
                    throw new ExistingServiceException("Passport number already exists");
                });

        clientRepo.findByPhone(client.getPhone())
                .ifPresent(c -> {
                    throw new ExistingServiceException("Phone already exists");
                });

        clientRepo.findByName(client.getName())
                .ifPresent(c -> {
                    throw new ExistingServiceException("Name already exists");
                });

        User user = userRepo.findByEmail(clientDTO.getEmail())
                .orElseThrow(() -> new NotFoundServiceException("User with email not found"));
        client.setUser(user);

        try {
            Client save = clientRepo.save(client);
            return clientMapper.clientToClientDTO(save);
        }catch (DataAccessException e) {
            throw new ServiceException("Error while creating client: " + e.getMessage());
        }
    }

    //9
    @Override
    public List<ClientDTO> getAllClients() {
        if (clientRepo.count() == 0) {
            throw new NotFoundServiceException("Clients not found");
        }

        return clientRepo.findAll().stream()
                .map(clientMapper::clientToClientDTO)
                .toList();
    }

    //10
    @Override
    //@PreAuthorize("@accessChecker.isClientOwner(#id)")
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client updatedClient = clientRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Client not found"));

        clientRepo.findByPassportNumber(clientDTO.getPassportNumber())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new ExistingServiceException("Passport number already exists");
                });

        clientRepo.findByPhone(clientDTO.getPhone())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new ExistingServiceException("Phone already exists");
                });

        clientRepo.findByName(clientDTO.getName())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new ExistingServiceException("Name already exists");
                });

        updatedClient.setName(clientDTO.getName());
        updatedClient.setPassportNumber(clientDTO.getPassportNumber());
        updatedClient.setPhone(clientDTO.getPhone());

       try {
           Client save = clientRepo.save(updatedClient);
           return clientMapper.clientToClientDTO(save);
       }catch (DataAccessException e) {
           throw new ServiceException("Error while updating client: " + e.getMessage());
       }
    }

    //11
    @Override
    //@PreAuthorize("@accessChecker.isClientOwner(#id)")
    public ClientDTO deleteClient(Long id) {
        Client existingClient = clientRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Client not found"));

        User user = existingClient.getUser();

        try {
            clientRepo.delete(existingClient);
            userRepo.delete(user);
            return clientMapper.clientToClientDTO(existingClient);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete client due to database error");
        }
    }

    //22
    @Override
    public List<ClientDTO> getClientsByCountry(Long countryId) {
        countryRepo.findById(countryId)
                .orElseThrow(() -> new NotFoundServiceException("Country not found"));

        List<Booking> bookings = bookingRepo.findBookingsByTourCountryId(countryId);

        List<Client> clients = bookings.stream()
                .map(Booking::getClient)
                .toList();

        if (clients.isEmpty()) {
            throw new NotFoundServiceException("No clients found who visited the country with id " + countryId);
        }

        return clients.stream()
                .map(clientMapper::clientToClientDTO)
                .toList();
    }
}
