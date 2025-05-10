package zadudoder.spmhelper.api;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zadudoder.spmhelper.api.types.Card;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class SPWorldsApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(SPWorldsApi.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String API_URL = "https://spworlds.ru/api/public/";

    public static int getBalance(Card card) {
        try {
            String authHeader = "Bearer " + Base64.getEncoder()
                    .encodeToString((card.id() + ":" + card.token()).getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "card"))
                    .header("Authorization", authHeader)
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP error: " + response.statusCode());
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            return json.get("balance").getAsInt();
        } catch (Exception e) {
            return -1; // Возвращаем -1 при ошибке
        }
    }

    public static JsonObject createTransfer(Card senderCard, String receiverCardNumber, int amount, String comment) {
        try {
            // 1. Формируем запрос для СП
            JsonObject item = new JsonObject();
            item.addProperty("name", "Перевод средств");
            item.addProperty("count", 1);
            item.addProperty("price", amount);
            if (comment != null && comment.length() >= 3) {
                item.addProperty("comment", comment);
            }

            JsonObject requestBody = new JsonObject();
            requestBody.add("items", new JsonArray());
            requestBody.getAsJsonArray("items").add(item);
            requestBody.addProperty("redirectUrl", "https://spworlds.ru/transfer/success");
            requestBody.addProperty("webhookUrl", "https://your-site.com/webhook");
            requestBody.addProperty("data", "to:" + receiverCardNumber); // Указываем получателя

            // 2. Отправляем от имени карты-отправителя
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "payments"))
                    .header("Authorization", "Bearer " +
                            Base64.getEncoder().encodeToString((senderCard.id() + ":" + senderCard.token()).getBytes()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Проверяем ответ
            if (response.statusCode() == 201) {
                return JsonParser.parseString(response.body()).getAsJsonObject();
            }
            throw new RuntimeException("Ошибка API: " + response.body());
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            return error;
        }
    }

    public static JsonObject getCardInfo(Card card) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "card"))
                    .header("Authorization", "Bearer " + card.getBase64Key())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            return error;
        }
    }
}