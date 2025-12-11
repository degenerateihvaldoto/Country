package project;

import project.data.database.DatabaseHandler;
import project.domain.repository.CountryRepository;
import project.view.MainWindow;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        CountryRepository repository = new DatabaseHandler();

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(repository);
            window.setVisible(true);
        });
    }
}