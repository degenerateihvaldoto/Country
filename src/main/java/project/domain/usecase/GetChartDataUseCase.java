package project.domain.usecase;

import project.domain.repository.CountryRepository;
import java.sql.SQLException;
import java.util.List;

public class GetChartDataUseCase {
    private final CountryRepository repository;

    public GetChartDataUseCase(CountryRepository repository) {
        this.repository = repository;
    }

    public List<CountryRepository.SubregionData> execute() throws SQLException {
        return repository.getSubregionPercentages();
    }
}