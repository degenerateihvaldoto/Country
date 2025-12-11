package project.domain.usecase;

import project.domain.repository.CountryRepository;
import java.sql.SQLException;
import java.util.List;

public class FindCountriesByPenetrationUseCase {
    private final CountryRepository repository;

    public FindCountriesByPenetrationUseCase(CountryRepository repository) {
        this.repository = repository;
    }

    public List<String> execute() throws SQLException {
        return repository.findCountriesByPenetration(75, 85);
    }
}