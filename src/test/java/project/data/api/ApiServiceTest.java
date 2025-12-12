package project.data.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiServiceTest {

    @Test
    void getCapital_WithValidCountry_ShouldReturnCapital() {
        String countryName = "Germany";
        String expectedCapital = "Berlin";

        String actualCapital = ApiService.getCapital(countryName);

        assertEquals(expectedCapital, actualCapital);
    }

    @Test
    void getCapital_WithInvalidCountry_ShouldReturnErrorString() {
        String invalidCountryName = "jdfvjdfvjndfvkjn";

        String response = ApiService.getCapital(invalidCountryName);

        assertTrue(response.contains("Не найдено") || response.contains("Ошибка"));
    }
}
