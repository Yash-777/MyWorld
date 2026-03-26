package com.github.yash777.postman;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class PasswordValidationTester {

    // Replace with your target API endpoint
    private static final String TARGET_URL = "https://example.com/api/validatePassword";

    public static void main(String[] args) {

        // Sample passwords to test
        List<String> passwords = Arrays.asList(
                "StrongPass@1234",
                "Yashwanth@1234",
                "Admin@123456",
                "MyPwd@123",
                "ValidPassphrase!Test2025"
        );

        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Create CSV file writer
        try (CSVWriter writer = new CSVWriter(new FileWriter("results.csv"))) {

            // Header row
            writer.writeNext(new String[]{"Password", "Status Code", "Response Message"});

            for (String password : passwords) {
                try {
                    // Prepare JSON payload (adjust based on API contract)
                    String jsonPayload = "{ \"password\": \"" + password + "\" }";

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(TARGET_URL))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                            .build();

                    // Send request
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    // Write result to CSV
                    writer.writeNext(new String[]{
                            password,
                            String.valueOf(response.statusCode()),
                            response.body()
                    });

                    System.out.println("Tested: " + password + " => " + response.statusCode());

                } catch (Exception e) {
                    writer.writeNext(new String[]{
                            password,
                            "ERROR",
                            e.getMessage()
                    });
                }
            }

            System.out.println("Results written to results.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
