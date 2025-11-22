package ua.ellka.touragency.service;

import ua.ellka.touragency.dto.ClientDTO;

import java.util.List;

public interface ClientService {
    ClientDTO createClient(ClientDTO clientDTO);
    List<ClientDTO> getAllClients();
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    ClientDTO deleteClient(Long id);
    List<ClientDTO> getClientsByCountry(Long countryId);
    ClientDTO getCurrentClient();
}
