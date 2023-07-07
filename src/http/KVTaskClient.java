package http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String apiToken;
    private final String urlServer;
    private final HttpClient client;
    Gson gson = Managers.getGson();

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

        HttpResponse<String> httpResponse;
        try {
            httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Получен код " + httpResponse.statusCode());
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
                JsonElement element = JsonParser.parseString(response.body());
                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    String value = gson.toJson(jsonObject);
                    System.out.format("loaded jsonObject: key = %s, value = %s\n", key, value);
                    return value;
                } else if (element.isJsonArray()) {
                    JsonArray jsonArray = element.getAsJsonArray();
                    String value = gson.toJson(jsonArray);
                    System.out.format("loaded jsonArray: key = %s, value = %s\n", key, value);
                    return value;
                } else if (element.isJsonPrimitive()) {
                    String value = element.getAsJsonPrimitive().toString();
                    System.out.format("loaded jsonPrimitive: key = %s, value = %s\n", key, value);
                    return value;
                }
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
