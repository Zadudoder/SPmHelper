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
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void startAuthProcess(FabricClientCommandSource source) {
        MinecraftClient client = MinecraftClient.getInstance();
        String playerUuid = client.player.getUuid().toString().replace("-", "");

        JsonObject json = new JsonObject();
        json.addProperty("minecraft_uuid", playerUuid);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/authorize"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String body = response.body();
                    JsonObject responseJson = JsonParser.parseString(body).getAsJsonObject();

                    if (responseJson.has("redirect_url")) {
                        String authUrl = responseJson.get("redirect_url").getAsString();
                        client.execute(() -> {
                            source.sendFeedback(Text.literal("§aПерейдите по ссылке для авторизации:"));
                            source.sendFeedback(Text.literal("§9" + authUrl));
                            Util.getOperatingSystem().open(authUrl);
                        });
                    } else {
                        String error = responseJson.has("error") ? responseJson.get("error").getAsString() :
                                "Неверный формат ответа";
                        client.execute(() -> source.sendError(Text.literal("§cОшибка: " + error)));
                    }
                    return null;
                })
                .exceptionally(e -> {
                    client.execute(() -> source.sendError(Text.literal("§cОшибка соединения: " + e.getMessage())));
                    return null;
                });
    }

    public static CompletableFuture<Boolean> makeCall(String service, String coordinates, String comment) {
        String token = SPmHelperClient.config.getAPI_TOKEN();
        if (token == null || token.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        JsonObject json = new JsonObject();
        json.addProperty("service", service);
        json.addProperty("coordinates", coordinates);
        json.addProperty("comment", comment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/calling"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return true;
                    } else {
                        System.err.println("Call failed: " + response.body());
                        return false;
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Call error: " + e.getMessage());
                    return false;
                });
    }

    public static CompletableFuture<Integer> getAuthStatus() {
        String token = SPmHelperClient.config.getAPI_TOKEN();
        if (token == null || token.isEmpty()) {
            return CompletableFuture.completedFuture(401);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/authorize/me"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode())
                .exceptionally(e -> -1);
    }
}
