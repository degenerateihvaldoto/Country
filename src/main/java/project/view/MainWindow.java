package project.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import project.data.api.ApiService;
import project.data.parser.CsvLoader;
import project.domain.model.Country;
import project.domain.repository.CountryRepository;
import project.domain.usecase.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {

    private final CountryRepository repository;
    private final FindMinUsersUseCase findMinUsersUseCase;
    private final FindCountriesByPenetrationUseCase findCountriesUseCase;
    private final GetChartDataUseCase getChartDataUseCase;

    private JTextArea logArea;
    private JPanel chartPanelContainer;

    public MainWindow(CountryRepository repository) {
        this.repository = repository;
        this.findMinUsersUseCase = new FindMinUsersUseCase(repository);
        this.findCountriesUseCase = new FindCountriesByPenetrationUseCase(repository);
        this.getChartDataUseCase = new GetChartDataUseCase(repository);

        setTitle("Проект Country");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton btnLoad = new JButton("1. Загрузить CSV в БД");
        JButton btnTask2 = new JButton("2. Задача: Вост. Европа");
        JButton btnTask3 = new JButton("3. Задача: 75-85%");
        JButton btnChart = new JButton("4. График");
        JButton btnApi = new JButton("5. API: Столица");

        topPanel.add(btnLoad);
        topPanel.add(btnTask2);
        topPanel.add(btnTask3);
        topPanel.add(btnChart);
        topPanel.add(btnApi);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.4);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        splitPane.setLeftComponent(new JScrollPane(logArea));

        chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.add(new JLabel("Здесь появится график", SwingConstants.CENTER));
        splitPane.setRightComponent(chartPanelContainer);
        add(splitPane, BorderLayout.CENTER);

        try {
            repository.initStorage();
            log("Программа запущена. БД инициализирована.");
        } catch (Exception e) {
            log("Ошибка инициализации БД: " + e.getMessage());
        }

        btnLoad.addActionListener(e -> new Thread(() -> {
            try {
                log("Начинаю загрузку из CSV...");
                List<Country> countries = CsvLoader.load("Country.csv");
                log("Прочитано строк: " + countries.size());
                repository.saveCountries(countries);
                log("Данные успешно сохранены в БД.");
            } catch (Exception ex) {
                log("Ошибка загрузки: " + ex.getMessage());
            }
        }).start());

        btnTask2.addActionListener(e -> {
            try {
                String result = findMinUsersUseCase.execute();
                log("\n--- ЗАДАНИЕ 2 ---\n" + result);
            } catch (Exception ex) {
                log("Ошибка SQL: " + ex.getMessage());
            }
        });

        btnTask3.addActionListener(e -> {
            try {
                List<String> list = findCountriesUseCase.execute();
                log("\n--- ЗАДАНИЕ 3 ---");
                for (String s : list) log(s);
            } catch (Exception ex) {
                log("Ошибка SQL: " + ex.getMessage());
            }
        });

        btnChart.addActionListener(e -> {
            try {
                List<CountryRepository.SubregionData> data = getChartDataUseCase.execute();
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                for (CountryRepository.SubregionData item : data) {
                    dataset.addValue(item.percentage, "Процент", item.subregionName);
                }
                JFreeChart chart = ChartFactory.createBarChart("Интернет по субрегионам", "Субрегион", "Процент (%)", dataset, PlotOrientation.HORIZONTAL, true, true, false);
                chartPanelContainer.removeAll();
                chartPanelContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
                chartPanelContainer.revalidate();
                chartPanelContainer.repaint();
                log("\nГрафик построен.");
            } catch (Exception ex) {
                log("Ошибка графика: " + ex.getMessage());
            }
        });

        btnApi.addActionListener(e -> {
            String country = JOptionPane.showInputDialog(this, "Введите название страны (англ):", "USA");
            if (country != null && !country.isEmpty()) {
                log("\nЗапрос к API для: " + country + "...");
                new Thread(() -> {
                    String capital = ApiService.getCapital(country);
                    SwingUtilities.invokeLater(() -> log("Столица " + country + ": " + capital));
                }).start();
            }
        });
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}