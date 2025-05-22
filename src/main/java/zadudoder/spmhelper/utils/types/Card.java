package zadudoder.spmhelper.utils.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import zadudoder.spmhelper.utils.SPWorldsApi;

public class Card {
    public String id;
    public String token;
    public int number;

    public Card() {
    } // Пустой конструктор для десериализации

    public Card(String id, String token) {
        this.id = id;
        this.token = token;
        JsonObject ownerInfo = SPWorldsApi.getOwnerInfo(this);
        JsonArray cards = ownerInfo.getAsJsonArray("cards");
        for (JsonElement card : cards) {
            if (card.getAsJsonObject().get("id").getAsString().equals(id)) {
                this.number = card.getAsJsonObject().get("number").getAsInt();
            }
        }
    }

    public String getBase64Key() {
        return java.util.Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }
}