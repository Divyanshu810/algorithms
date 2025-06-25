package Adyen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class CountryPopulationChecker {

    public static int getCountries(String S, int P) {
        int count = 0;
        int page = 1;
        int totalPages = 1;

        try {
            while (page <= totalPages) {
                String urlString = String.format("https://jsonmock.hackerrank.com/api/countries/search?name=%s&page=%d", S, page);
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                in.close();
                conn.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());

                // Set totalPages on first iteration
                if (page == 1) {
                    totalPages = jsonResponse.getInt("total_pages");
                }

                JSONArray data = jsonResponse.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject country = data.getJSONObject(i);
                    String name = country.getString("name");
                    int population = country.getInt("population");

                    if (name.toLowerCase().contains(S.toLowerCase()) && population > P) {
                        count++;
                    }
                }

                page++; // Move to next page
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public static void main(String[] args) {
        // Test Case
        System.out.println(getCountries("sing", 5000000));  // Example output
    }
}
