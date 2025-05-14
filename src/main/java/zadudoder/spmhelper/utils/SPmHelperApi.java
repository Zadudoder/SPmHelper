package zadudoder.spmhelper.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import zadudoder.spmhelper.SPmHelperClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;


public class SPmHelperApi {
    private static final String API_BASE = "https://api-spmhelpers.sp-mini.ru/api";

    public static void startAuthProcess(FabricClientCommandSource source) {
        MinecraftClient client = MinecraftClient.getInstance();
        String playerUuid = client.player.getUuid().toString().replace("-", "");
        System.out.println("Starting auth process for UUID: " + playerUuid);

        source.sendFeedback(Text.literal("§aНачинаем процесс авторизации через Discord..."));

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        String url = API_BASE + "/authorize";
        System.out.println("POST to: " + url);

        JsonObject json = new JsonObject();
        json.addProperty("minecraft_uuid", playerUuid);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int status = response.statusCode();
                    String body = response.body();
                    System.out.println("Response: " + status + " - " + body);
                    return body;
                })
                .thenApply(body -> JsonParser.parseString(body).getAsJsonObject())
                .thenAccept(responseJson -> {
                    if (responseJson.has("redirect_url")) {
                        String authUrl = responseJson.get("redirect_url").getAsString();
                        client.execute(() -> {
                            source.sendFeedback(Text.literal("§aПерейдите по ссылке для авторизации:"));
                            source.sendFeedback(Text.literal("§9" + authUrl));
                            Util.getOperatingSystem().open(authUrl);
                        });
                    } else {
                        String error = responseJson.has("error") ? responseJson.get("error").getAsString() :
                                "Неверный формат ответа (ожидалось поле redirect_url)";
                        client.execute(() -> {
                            source.sendError(Text.literal("§cОшибка API: " + error));
                        });
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Auth error: " + e);
                    e.printStackTrace();
                    client.execute(() -> {
                        source.sendError(Text.literal("§cОшибка соединения: " + e.getMessage()));
                    });
                    return null;
                });
    }

    public static CompletableFuture<Integer> getAuthStatus(FabricClientCommandSource source) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient httpClient = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                String url = API_BASE + "/authorize/me";

                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(15))  // Добавляем общий таймаут
                        .GET();

                if (SPmHelperClient.config.getAPI_TOKEN() != null) {
                    requestBuilder.header("Authorization", "Bearer " + SPmHelperClient.config.getAPI_TOKEN());
                }

                HttpResponse<String> response = httpClient.send(
                        requestBuilder.build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                return response.statusCode();
            } catch (IOException | InterruptedException e) {
                return -1;  // Или можно бросить CompletionException
            }
        });
    }
}
