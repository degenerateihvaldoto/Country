package project.data.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.domain.model.Country;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvLoaderTest {

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test-country-data", ".csv");
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null) {
            tempFile.delete();
        }
    }

    @Test
    void load_ShouldCorrectlyParseFile() throws Exception {
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Country or area,Subregion,Region,Internet users,Population\n");
            writer.write("\"Testland\",\"Test Subregion\",\"Test Region\",\"1,234,567\",\"2,000,000\"\n");
            writer.write("Emptydata,,,,\n");
        }

        List<Country> countries = CsvLoader.load(tempFile.getAbsolutePath());

        assertEquals(2, countries.size(), "Должно быть загружено 2 страны");

        Country firstCountry = countries.get(0);
        assertEquals("Testland", firstCountry.getName());
        assertEquals("Test Subregion", firstCountry.getSubregion());
        assertEquals(1234567L, firstCountry.getInternetUsers(), "Парсинг числа с запятыми для пользователей");
        assertEquals(2000000L, firstCountry.getPopulation(), "Парсинг числа с запятыми для населения");

        Country secondCountry = countries.get(1);
        assertEquals(0L, secondCountry.getInternetUsers(), "Пустое значение должно парситься как 0");
        assertEquals(0L, secondCountry.getPopulation(), "Пустое значение должно парситься как 0");
    }

    @Test
    void parseNumber_ShouldHandleVariousFormats() {
        assertEquals(1000L, CsvLoader.parseNumber("1,000"));
        assertEquals(500L, CsvLoader.parseNumber("500"));
        assertEquals(0L, CsvLoader.parseNumber(""));
        assertEquals(0L, CsvLoader.parseNumber(null));
        assertEquals(1234567890L, CsvLoader.parseNumber("1,234,567,890"));
    }
}