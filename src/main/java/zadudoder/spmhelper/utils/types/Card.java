package zadudoder.spmhelper.utils.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import zadudoder.spmhelper.utils.SPWorldsApi;

public class Card {
    public String id;
    public String token;
    public String number;

    public Card() {
    }

    public Card(String id, String token) {
        this.id = id;
        this.token = token;
        JsonObject ownerInfo = SPWorldsApi.getOwnerInfo(this);
        JsonArray cards = ownerInfo.getAsJsonArray("cards");
        if (cards == null) {
            this.id = null;
            this.token = null;
            MinecraftClient.getInstance().getToastManager().add(SystemToast.create(MinecraftClient.getInstance(), SystemToast.Type.NARRATOR_TOGGLE, Text.of("Ошибочка!"), Text.of("Вы недавно поменяли никнейм, подождите и повторите добавление карты")));
        } else {
            for (JsonElement card : cards) {
                if (card.getAsJsonObject().get("id").getAsString().equals(id)) {
                    this.number = card.getAsJsonObject().get("number").getAsString();
                }
            }
        }
    }

    public String getBase64Key() {
        return java.util.Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }
}