// api/SPWorldsApi.java
package zadudoder.spmhelper.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import zadudoder.spmhelper.api.types.Card;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class SPWorldsApi {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String API_URL = "https://spworlds.ru/api/public/";

    public static int getBalance(Card card) {
        try {
            String authHeader = "Bearer " + Base64.getEncoder()
                    .encodeToString((card.id() + ":" + card.token()).getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "card"))
                    .header("Authorization", authHeader)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            return json.get("balance").getAsInt();
        } catch (Exception e) {
            return -5298;
        }
    }
}