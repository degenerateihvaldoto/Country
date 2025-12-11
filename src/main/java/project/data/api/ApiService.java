package project.data.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiService {

    public static String getCapital(String countryName) {
        try {
            String encoded = countryName.replace(" ", "%20");
            URL url = new URL("https://restcountries.com/v3.1/name/" + encoded + "?fields=capital");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);

            if (conn.getResponseCode() != 200) {
                return "Не найдено (или ошибка сети)";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String json = response.toString();
            int start = json.indexOf("[\"");
            int end = json.indexOf("\"]");

            if (start != -1 && end != -1) {
                return json.substring(start + 2, end);
            }
            return "Данные получены, но формат сложный";

        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }
}