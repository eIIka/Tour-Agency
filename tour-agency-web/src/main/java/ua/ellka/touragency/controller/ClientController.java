package ua.ellka.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.ellka.touragency.dto.ClientDTO;
import ua.ellka.touragency.service.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/client")
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> allClients = clientService.getAllClients();

        return ResponseEntity.ok(allClients);
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<ClientDTO>> getClientsByCountry(@PathVariable(name = "countryId") Long countryId) {
        List<ClientDTO> clientsByCountry = clientService.getClientsByCountry(countryId);

        return ResponseEntity.ok(clientsByCountry);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {
        ClientDTO createdClient = clientService.createClient(clientDTO);

        return ResponseEntity.ok(createdClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable(name = "id") Long id, @RequestBody ClientDTO clientDTO) {
        ClientDTO updatedClient = clientService.updateClient(id, clientDTO);

        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ClientDTO> deleteClient(@PathVariable(name = "id") Long id) {
        ClientDTO deletedClient = clientService.deleteClient(id);

        return ResponseEntity.ok(deletedClient);
    }
}
