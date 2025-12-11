package project.domain.usecase;

import org.junit.jupiter.api.Test;
import project.domain.model.Country;
import project.domain.repository.CountryRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindMinUsersUseCaseTest {

    @Test
    void execute_shouldReturnDataFromRepository() throws SQLException {
        // 1. Arrange (Подготовка)
        // Создаем фейковый репозиторий (мок), который не ходит в базу.
        CountryRepository mockRepository = new MockCountryRepository();
        // Создаем UseCase и "подсовываем" ему наш фейковый репозиторий.
        FindMinUsersUseCase useCase = new FindMinUsersUseCase(mockRepository);

        // 2. Act (Действие)
        // Выполняем бизнес-логику. UseCase думает, что работает с реальной БД,
        // но на самом деле он вызовет метод нашего мока.
        String result = useCase.execute();

        // 3. Assert (Проверка)
        // Проверяем, что UseCase просто вернул те данные, которые мы "запрограммировали" в моке.
        assertEquals("Fake Country with Min Users", result);
    }


    private static class MockCountryRepository implements CountryRepository {
        @Override
        public void initStorage() { /* Не реализуем, т.к. не нужно для этого теста */ }

        @Override
        public void saveCountries(List<Country> countries) { /* Не реализуем */ }

        @Override
        public String findMinUsersInEasternEurope() {
            // Возвращаем заранее известный, жестко заданный результат для теста.
            // Это "ответ" нашего мока.
            return "Fake Country with Min Users";
        }

        @Override
        public List<String> findCountriesByPenetration(double from, double to) {
            return Collections.emptyList();
        }

        @Override
        public List<SubregionData> getSubregionPercentages() {
            return Collections.emptyList();
        }
    }
}