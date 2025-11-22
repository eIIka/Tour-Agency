package ua.ellka.touragency.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TourDTO {
    private Long id;
    private String name;
    private Long countryId;
    private String countryName;
    private String countryRegion;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private Long guideId;
    private String guideName;
    private String imageUrl;
}
