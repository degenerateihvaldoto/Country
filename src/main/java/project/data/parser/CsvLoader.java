package project.data.parser;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import project.domain.model.Country;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public static List<Country> load(String fileName) throws IOException, CsvException {
        List<Country> countries = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 5) continue;
                long internetUsers = parseNumber(line[3]);
                long population = parseNumber(line[4]);
                countries.add(new Country(line[0], line[1], line[2], internetUsers, population));
            }
        }
        return countries;
    }

    public static long parseNumber(String rawNumber) {
        if (rawNumber == null || rawNumber.trim().isEmpty()) return 0;
        String cleanNumber = rawNumber.replace(",", "").trim();
        try {
            return Long.parseLong(cleanNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}