package zadudoder.spmhelper.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import zadudoder.spmhelper.config.SPmHelperConfig;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class SocketClient extends WebSocketClient {
    private final CompletableFuture<String> responseFuture = new CompletableFuture<>();
    private Runnable onOpenCallback;
    private ClientPlayerEntity clientPlayer;

    public SocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        if (onOpenCallback != null) {
            onOpenCallback.run(); // Уведомляем, что соединение открыто
        }

    }

    @Override
    public void onMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        JsonObject responseJson = JsonParser.parseString(message).getAsJsonObject();
        System.out.println(responseJson.toString());
        if (responseJson.has("auth_url")) {
            clientPlayer.sendMessage(Text.literal("§a[SPmHelper]: Открытие ссылки на авторизацию"));
            String authUrl = responseJson.get("auth_url").getAsString();
            client.execute(() -> {
                Util.getOperatingSystem().open(authUrl);
            });
            clientPlayer.sendMessage(Text.literal("§a[SPmHelper]: Ожидание авторизации"));
        } else if (responseJson.has("token")) {
            SPmHelperConfig.get().setAPI_TOKEN(responseJson.get("token").getAsString());
            AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
            clientPlayer.sendMessage(Text.literal("§a[SPmHelper]: Токен успешно записан!"));
            safeClose();
        } else if (responseJson.has("error")) {
            if (responseJson.get("error").getAsString().equals("Авторизация отклонена игроком")) {
                clientPlayer.sendMessage(Text.literal("§c[SPmHelper]: Авторизация отменена вами"));
            } else {
                clientPlayer.sendMessage(Text.literal("§c[SPmHelper]: Не удалось получить ссылку на авторизацию"));
            }
            safeClose();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
    }

    @Override
    public void onError(Exception e) {
        responseFuture.completeExceptionally(e);
    }

    public void setOnOpenCallback(Runnable callback) {
        this.onOpenCallback = callback;
    }

    public void setClientPlayer(ClientPlayerEntity client) {
        this.clientPlayer = client;
    }

    private void safeClose() {
        if (isOpen()) {
            close();
        }
    }
}
