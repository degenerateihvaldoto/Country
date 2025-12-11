package project.data.database;

import project.domain.model.Country;
import project.domain.repository.CountryRepository;
import java.util.Locale;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler implements CountryRepository {

    private final String dbUrl;
    private final Connection sharedConnection; // для тестов
    private final boolean isExternalConnection; // флаг управляющий закрытием

    public DatabaseHandler() {
        this.dbUrl = "jdbc:sqlite:project_db.sqlite";
        this.sharedConnection = null;
        this.isExternalConnection = false;
    }

    public DatabaseHandler(Connection sharedConnection) {
        this.dbUrl = null;
        this.sharedConnection = sharedConnection;
        this.isExternalConnection = true;
    }

    private Connection getConnection() throws SQLException {
        if (isExternalConnection) {
            return this.sharedConnection;
        }
        return DriverManager.getConnection(this.dbUrl);
    }

    private void closeConnection(Connection conn) throws SQLException {
        if (!isExternalConnection && conn != null) {
            conn.close();
        }
    }

    @Override
    public void initStorage() throws SQLException {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS countries");
            stmt.execute("DROP TABLE IF EXISTS subregions");
            stmt.execute("DROP TABLE IF EXISTS regions");

            stmt.execute("CREATE TABLE regions (id INTEGER PRIMARY KEY, name TEXT UNIQUE)");
            stmt.execute("CREATE TABLE subregions (id INTEGER PRIMARY KEY, name TEXT UNIQUE, region_id INTEGER, FOREIGN KEY(region_id) REFERENCES regions(id))");
            stmt.execute("CREATE TABLE countries (id INTEGER PRIMARY KEY, name TEXT, internet_users INTEGER, population INTEGER, subregion_id INTEGER, FOREIGN KEY(subregion_id) REFERENCES subregions(id))");
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void saveCountries(List<Country> countries) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

            String insertRegionSQL = "INSERT OR IGNORE INTO regions(name) VALUES (?)";
            String getRegionIdSQL = "SELECT id FROM regions WHERE name = ?";
            String insertSubregionSQL = "INSERT OR IGNORE INTO subregions(name, region_id) VALUES (?, ?)";
            String getSubregionIdSQL = "SELECT id FROM subregions WHERE name = ?";
            String insertCountrySQL = "INSERT INTO countries(name, subregion_id, internet_users, population) VALUES (?, ?, ?, ?)";

            try (PreparedStatement psRegion = conn.prepareStatement(insertRegionSQL);
                 PreparedStatement psGetRegionId = conn.prepareStatement(getRegionIdSQL);
                 PreparedStatement psSub = conn.prepareStatement(insertSubregionSQL);
                 PreparedStatement psGetSubId = conn.prepareStatement(getSubregionIdSQL);
                 PreparedStatement psCountry = conn.prepareStatement(insertCountrySQL)) {

                for (Country c : countries) {
                    psRegion.setString(1, c.getRegion());
                    psRegion.executeUpdate();
                    psGetRegionId.setString(1, c.getRegion());
                    int regionId = getSingleIntResult(psGetRegionId);

                    psSub.setString(1, c.getSubregion());
                    psSub.setInt(2, regionId);
                    psSub.executeUpdate();
                    psGetSubId.setString(1, c.getSubregion());
                    int subregionId = getSingleIntResult(psGetSubId);

                    psCountry.setString(1, c.getName());
                    psCountry.setInt(2, subregionId);
                    psCountry.setLong(3, c.getInternetUsers());
                    psCountry.setLong(4, c.getPopulation());
                    psCountry.executeUpdate();
                }
            }
            conn.commit();
        } finally {
            closeConnection(conn);
        }
    }

    private int getSingleIntResult(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    @Override
    public String findMinUsersInEasternEurope() throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT c.name, c.internet_users FROM countries c JOIN subregions s ON c.subregion_id = s.id WHERE s.name = 'Eastern Europe' ORDER BY c.internet_users ASC LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return String.format(Locale.US, "%s (%,d чел.)", rs.getString("name"), rs.getLong("internet_users"));
            }
        } finally {
            closeConnection(conn);
        }
        return "Не найдено.";
    }

    @Override
    public List<SubregionData> getSubregionPercentages() throws SQLException {
        List<SubregionData> results = new ArrayList<>();
        Connection conn = getConnection();
        String sql = "SELECT s.name, (SUM(c.internet_users) * 100.0 / SUM(c.population)) as percentage " +
                "FROM countries c JOIN subregions s ON c.subregion_id = s.id " +
                "GROUP BY s.name HAVING SUM(c.population) > 0 ORDER BY percentage DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new SubregionData(rs.getString("name"), rs.getDouble("percentage")));
            }
        } finally {
            closeConnection(conn);
        }
        return results;
    }

    @Override
    public List<String> findCountriesByPenetration(double from, double to) throws SQLException {
        List<String> results = new ArrayList<>();
        Connection conn = getConnection();
        String sql = "SELECT name, (internet_users * 100.0 / population) as prc " +
                "FROM countries WHERE (internet_users * 100.0 / population) BETWEEN ? AND ? ORDER BY prc DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, from);
            stmt.setDouble(2, to);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(String.format("%s (%.2f%%)", rs.getString("name"), rs.getDouble("prc")));
                }
            }
        } finally {
            closeConnection(conn);
        }
        return results;
    }
}