package project.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountryTest {

    @Test
    void constructorAndGetters_ShouldWorkCorrectly() {
        // готовим тестовые данные
        String name = "Testland";
        String subregion = "Test Subregion";
        String region = "Test Region";
        long users = 100L;
        long population = 200L;

        Country country = new Country(name, subregion, region, users, population);

        // проверяем что все геттеры возвращают правильные значения
        assertEquals(name, country.getName());
        assertEquals(subregion, country.getSubregion());
        assertEquals(region, country.getRegion());
        assertEquals(users, country.getInternetUsers());
        assertEquals(population, country.getPopulation());
    }
}