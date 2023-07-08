package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String apiToken;
    private final String urlServer;
    private final HttpClient client;

    public KVTaskClient(String urlServer) {
        System.out.println("url = " + urlServer);
        this.urlServer = urlServer;
        client = HttpClient.newHttpClient();
        register();
    }

    private void register() {
        URI url = URI.create(getRegisterUrl());
        System.out.println("register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        System.out.println("prepared request - register");
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("got response" + response);

            if (response.statusCode() == 200) {
                apiToken = response.body();
                System.out.println(this.getClass().getName() + " получен токен: " + apiToken);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            System.out.println("exception");
            exception.printStackTrace();
        }
    }

    public void put(String key, String json) {
        URI url = URI.create(getSaveUrl(key));
        System.out.format("Клиент передал key = %s, value = %s\n", key, json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println(this.getClass().getName() + " получен код : " + response.statusCode());
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public String load(String key) {
        URI url = URI.create(getLoadUrl(key));
        System.out.format("Клиент передал key = %s\n", key);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String getRegisterUrl() {
        return String.format("%s/register", urlServer);
    }

    private String getSaveUrl(String key) {
        return String.format("%s/save/%s?API_TOKEN=%s", urlServer, key, apiToken);
    }

    private String getLoadUrl(String key) {
        return String.format("%s/load/%s?API_TOKEN=%s", urlServer, key, apiToken);
    }

}
