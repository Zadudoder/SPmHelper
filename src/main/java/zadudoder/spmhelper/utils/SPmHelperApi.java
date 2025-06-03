package zadudoder.spmhelper.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import zadudoder.spmhelper.config.SPmHelperConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;


public class SPmHelperApi {
    private static final String API_BASE = "https://api.spmhelper.ru/api";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void startAuthProcess(ClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        String playerUuid = client.player.getUuid().toString().replace("-", "");
        JsonObject json = new JsonObject();
        json.addProperty("minecraft_uuid", playerUuid);
        try {
            SocketClient socketClient = new SocketClient(new URI("wss://api.spmhelpers.ru/api/authorize/ws"));
            socketClient.setOnOpenCallback(() -> {
                player.sendMessage(Text.literal("§a[SPmHelper]: Получение ссылки на авторизации"));
                socketClient.send(json.toString());
            });
            socketClient.setClientPlayer(player);
            socketClient.connect();
        } catch (URISyntaxException e) {
            player.sendMessage(Text.literal("§c[SPmHelper]: Ошибка подключения к серверу"));
        } catch (IllegalStateException Exception) {
            player.sendMessage(Text.literal("§c[SPmHelper]: Ошибка подключения к WebSocket"));
        } catch (Exception e) {
            player.sendMessage(Text.literal("§c[SPmHelper]: Ошибка сервера"));
        }


    }

    public static CompletableFuture<Boolean> makeCall(String service, String coordinates, String comment) {
        String token = SPmHelperConfig.get().getAPI_TOKEN();
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
        String token = SPmHelperConfig.get().getAPI_TOKEN();
        if (token == null || token.isEmpty()) {
            return CompletableFuture.completedFuture(401);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/authorize/me"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .exceptionally(e -> -1);
    }

    public static JsonObject getModVersionInfo(int index) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/versions"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body()).getAsJsonArray().get(index).getAsJsonObject();
        } catch (Exception ex) {
            return null;
        }
    }

    public static JsonObject getLastModVersionInfo() {
        return getModVersionInfo(0);
    }

    public static int getAPIStatus() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/info"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (Exception ex) {
            return 404;
        }
    }
}
