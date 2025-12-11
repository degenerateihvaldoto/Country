package project.data.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.domain.model.Country;
import project.domain.repository.CountryRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseHandlerTest {

    private Connection connection; // Соединение, которое будет жить на протяжении всего теста
    private DatabaseHandler dbHandler;

    @BeforeEach
    void setUp() throws SQLException {
        // Создаем ОДНО соединение для in-memory базы
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        // Создаем DatabaseHandler, передавая ему это единственное соединение
        dbHandler = new DatabaseHandler(connection);

        // Создаем таблицы
        dbHandler.initStorage();

        // Готовим и сохраняем тестовые данные
        List<Country> testCountries = Arrays.asList(
                new Country("Belarus", "Eastern Europe", "Europe", 7_000_000, 9_500_000),
                new Country("Poland", "Eastern Europe", "Europe", 34_000_000, 38_000_000),
                new Country("Ukraine", "Eastern Europe", "Europe", 31_000_000, 44_000_000),
                new Country("Bermuda", "Northern America", "Americas", 60_000, 62_000)
        );
        dbHandler.saveCountries(testCountries);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Закрываем соединение после теста, чтобы очистить ресурсы
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void findMinUsersInEasternEurope_ShouldReturnBelarus() throws SQLException {
        // были проблемы с форматированием US
        String expected = "Belarus (7,000,000 чел.)";
        String actual = dbHandler.findMinUsersInEasternEurope();
        assertEquals(expected, actual);
    }

    @Test
    void findCountriesByPenetration_ShouldReturnCorrectCountries() throws SQLException {
        List<String> results = dbHandler.findCountriesByPenetration(70, 80);
        assertEquals(2, results.size());
        assertTrue(results.get(0).contains("Belarus"));
    }

    @Test
    void getSubregionPercentages_ShouldCalculateCorrectly() throws SQLException {
        List<CountryRepository.SubregionData> percentages = dbHandler.getSubregionPercentages();
        CountryRepository.SubregionData easternEuropeData = percentages.stream()
                .filter(d -> d.subregionName.equals("Eastern Europe"))
                .findFirst()
                .orElse(null);
        assertNotNull(easternEuropeData);
        assertEquals(78.69, easternEuropeData.percentage, 0.01);
    }
}