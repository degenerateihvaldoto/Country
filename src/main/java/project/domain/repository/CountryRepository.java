package project.domain.repository;

import project.domain.model.Country;
import java.sql.SQLException;
import java.util.List;

public interface CountryRepository {
    void initStorage() throws SQLException;
    void saveCountries(List<Country> countries) throws SQLException;
    String findMinUsersInEasternEurope() throws SQLException;
    List<String> findCountriesByPenetration(double from, double to) throws SQLException;
    List<SubregionData> getSubregionPercentages() throws SQLException;

    class SubregionData {
        public String subregionName;
        public double percentage;
        public SubregionData(String name, double percent) {
            this.subregionName = name;
            this.percentage = percent;
        }
    }
}