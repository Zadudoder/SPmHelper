package zadudoder.spmhelper.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.types.BaseCard;
import zadudoder.spmhelper.utils.types.Card;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SPWorldsApi {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String API_URL = "https://spworlds.ru/api/public/";

    public static int getBalance(Card card) {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "card"))
                    .header("Authorization", getAuthorizationHeader(card))
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
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("receiver", receiverCardNumber);
            requestBody.addProperty("amount", amount);
            if (comment.isEmpty()) {
                comment = " ";
            }
            requestBody.addProperty("comment", MinecraftClient.getInstance().getSession().getUsername() + ": " + comment);

            // 2. Отправляем от имени карты-отправителя
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "transactions"))
                    .header("Authorization", getAuthorizationHeader(senderCard))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body()).getAsJsonObject();
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
                    .header("Authorization", getAuthorizationHeader(card))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            return error;
        }
    }

    public static JsonObject getOwnerInfo(Card card) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "accounts/me"))
                    .header("Authorization", getAuthorizationHeader(card))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            return error;
        }
    }

    public  static BaseCard[] getCards(String nick){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "accounts/"+nick+"/cards"))
                    .header("Authorization", getAuthorizationHeader(SPmHelperConfig.get().getMainCard()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray cardsArray = JsonParser.parseString(response.body()).getAsJsonArray();
            Gson gson = new Gson();
            BaseCard[] cards = gson.fromJson(cardsArray, BaseCard[].class);
            return cards;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getAuthorizationHeader(Card card) {
        return "Bearer " + card.getBase64Key();
    }
}