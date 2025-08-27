package ua.ellka.touragency.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String email;
    private String password;
    private String role;
    private String name;
    private String passportNumber;
    private String phone;
    private String language;
}
