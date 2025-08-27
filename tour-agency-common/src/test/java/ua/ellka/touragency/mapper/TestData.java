package ua.ellka.touragency.mapper;

import ua.ellka.touragency.dto.*;
import ua.ellka.touragency.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestData {

    public static final Long USER_ID = 1L;
    public static final String USER_EMAIL = "test@gmail.com";
    public static final String USER_PASSWORD = "test123";
    public static final String USER_ROLE = "ROLE_USER";

    public static User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);
        user.setRole(USER_ROLE);
        return user;
    }

    public static UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(USER_ID);
        userDTO.setEmail(USER_EMAIL);
        userDTO.setRole(USER_ROLE);
        return userDTO;
    }

    public static final User USER = createUser();
    public static final UserDTO USER_DTO = createUserDTO();

    public static final Long CLIENT_ID = 1L;
    public static final String CLIENT_NAME = "Client Name";
    public static final String CLIENT_PASSPORT_NUMBER = "AA123456";
    public static final String CLIENT_PHONE = "+380999999999";

    public static Client createClient() {
        Client client = new Client();
        client.setId(CLIENT_ID);
        client.setName(CLIENT_NAME);
        client.setPassportNumber(CLIENT_PASSPORT_NUMBER);
        client.setPhone(CLIENT_PHONE);
        client.setUser(USER);
        return client;
    }

    public static ClientDTO createClientDTO() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(CLIENT_ID);
        clientDTO.setName(CLIENT_NAME);
        clientDTO.setPassportNumber(CLIENT_PASSPORT_NUMBER);
        clientDTO.setPhone(CLIENT_PHONE);
        clientDTO.setEmail(USER_EMAIL);
        return clientDTO;
    }

    public static final Long COUNTRY_ID = 1L;
    public static final String COUNTRY_NAME = "Country Name";
    public static final String COUNTRY_REGION = "Country Region";

    public static Country createCountry() {
        Country country = new Country();
        country.setId(COUNTRY_ID);
        country.setName(COUNTRY_NAME);
        country.setRegion(COUNTRY_REGION);
        return country;
    }

    public static CountryDTO createCountryDTO() {
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setId(COUNTRY_ID);
        countryDTO.setName(COUNTRY_NAME);
        countryDTO.setRegion(COUNTRY_REGION);
        return countryDTO;
    }

    public static final Long GUIDE_ID = 1L;
    public static final String GUIDE_NAME = "Guide Name";
    public static final String GUIDE_LANGUAGE = "Guide Language";

    public static Guide createGuide() {
        Guide guide = new Guide();
        guide.setId(GUIDE_ID);
        guide.setName(GUIDE_NAME);
        guide.setLanguage(GUIDE_LANGUAGE);
        guide.setUser(USER);
        return guide;
    }

    public static GuideDTO createGuideDTO() {
        GuideDTO guideDTO = new GuideDTO();
        guideDTO.setId(GUIDE_ID);
        guideDTO.setName(GUIDE_NAME);
        guideDTO.setLanguage(GUIDE_LANGUAGE);
        guideDTO.setEmail(USER_EMAIL);
        return guideDTO;
    }

    public static final Country COUNTRY = createCountry();
    public static final CountryDTO COUNTRY_DTO = createCountryDTO();

    public static final Guide GUIDE = createGuide();
    public static final GuideDTO GUIDE_DTO = createGuideDTO();

    public static final Long TOUR_ID = 1L;
    public static final String TOUR_NAME = "Tour Name";
    public static final LocalDate TOUR_START_DATE = LocalDate.of(2020, 1, 1);
    public static final LocalDate TOUR_END_DATE = LocalDate.of(2020, 2, 28);
    public static final BigDecimal TOUR_PRICE = BigDecimal.valueOf(200.0);

    public static Tour createTour() {
        Tour tour = new Tour();
        tour.setId(TOUR_ID);
        tour.setName(TOUR_NAME);
        tour.setCountry(COUNTRY);
        tour.setStartDate(TOUR_START_DATE);
        tour.setEndDate(TOUR_END_DATE);
        tour.setPrice(TOUR_PRICE);
        tour.setGuide(GUIDE);
        return tour;
    }

    public static TourDTO createTourDTO() {
        TourDTO tourDTO = new TourDTO();
        tourDTO.setId(TOUR_ID);
        tourDTO.setName(TOUR_NAME);
        tourDTO.setCountryId(COUNTRY_DTO.getId());
        tourDTO.setStartDate(TOUR_START_DATE);
        tourDTO.setEndDate(TOUR_END_DATE);
        tourDTO.setPrice(TOUR_PRICE);
        tourDTO.setGuideId(GUIDE_DTO.getId());
        return tourDTO;
    }

    public static final Tour TOUR = createTour();
    public static final TourDTO TOUR_DTO = createTourDTO();

    public static final Client CLIENT = createClient();
    public static final ClientDTO CLIENT_DTO = createClientDTO();

    public static final Long BOOKING_ID = 1L;
    public static final LocalDate BOOKING_DATE = LocalDate.of(2020, 1, 1);

    public static final Booking BOOKING = new Booking(BOOKING_ID, TOUR, CLIENT, BOOKING_DATE);
    public static final BookingDTO BOOKING_DTO = new BookingDTO(BOOKING_ID, TOUR.getId(), CLIENT.getId(), BOOKING_DATE);

}
