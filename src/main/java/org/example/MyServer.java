package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.sun.net.httpserver.HttpServer;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class MyServer {

    private final static String BASE_PHONE_SERVER = "http://192.168.3.82:8080/";

    public static void main(String[] args) throws IOException {
//        startServer();
        getRequest(BASE_PHONE_SERVER);
    }

    public static class ServerResponse{

        @SerializedName("message")
        String message;

        @SerializedName("model")
        String model;

        @SerializedName("version")
        String version;

        @SerializedName("manufactured")
        String manufactured;

        @SerializedName("battery")
        int battery;

        @SerializedName("code")
        String code;

        int test;

        
    }

    private static void getRequest(String baseUrl){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(baseUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new Gson();
                ServerResponse serverResponse = gson.fromJson(response.body().string(), ServerResponse.class);

                System.out.printf("Сообщение: %s%n", serverResponse.message);
                System.out.printf("Модель: %s%n", serverResponse.model);
                System.out.printf("Версия: %s%n", serverResponse.version);
                System.out.printf("Производитель: %s%n", serverResponse.manufactured);
                System.out.printf("Заряд батареи: %s%n", serverResponse.battery);
                System.out.printf("Код: %s%n", serverResponse.code);
            }
        });
    }

    private static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/hello", exchange -> {
            int code = 200;

            JsonObject json = new JsonObject();
            json.addProperty("message", "Привет мир!");
            json.addProperty("code", code);

            String response1 = json.toString();
            byte[] bytes1 = response1.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(code, bytes1.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes1);
            }
        });

        server.start();
        System.out.println("Сервер запущен: http://localhost:8080/hello");
    }
}