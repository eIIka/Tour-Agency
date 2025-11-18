package ua.ellka.touragency.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuideDTO {
    private Long id;
    private String name;
    private String language;
    private String email;
}
