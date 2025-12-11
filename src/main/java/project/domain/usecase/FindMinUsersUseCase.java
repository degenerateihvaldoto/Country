package project.domain.usecase;

import project.domain.repository.CountryRepository;
import java.sql.SQLException;

public class FindMinUsersUseCase {
    private final CountryRepository repository;

    public FindMinUsersUseCase(CountryRepository repository) {
        this.repository = repository;
    }

    public String execute() throws SQLException {
        return repository.findMinUsersInEasternEurope();
    }
}