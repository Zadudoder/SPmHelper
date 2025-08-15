package zadudoder.spmhelper.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import zadudoder.spmhelper.config.SPmHelperConfig;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class SocketClient implements WebSocket.Listener {
    private final CountDownLatch latch;
    private final StringBuilder aggregatedText = new StringBuilder();
    private final ClientPlayerEntity clientPlayer;

    public SocketClient(int latch, ClientPlayerEntity clientPlayer) {
        this.latch = new CountDownLatch(latch);
        this.clientPlayer = clientPlayer;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        //System.out.println("🔵 Соединение установлено");
        webSocket.request(1); // Запрашиваем первое сообщение
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        //System.out.println("📨 Получено сообщение: " + data);
        JsonObject responseJson = JsonParser.parseString(data.toString()).getAsJsonObject();
        if (last) {
            if (responseJson.has("auth_url")) {
                clientPlayer.sendMessage(Text.translatable("text.spmhelper.WebSocketClient_OpenURL"));
                String authUrl = responseJson.get("auth_url").getAsString();
                MinecraftClient.getInstance().execute(() -> {
                    Util.getOperatingSystem().open(authUrl);
                });
                clientPlayer.sendMessage(Text.translatable("text.spmhelper.WebSocketClient_WaitingAuth"));
            } else if (responseJson.has("token")) {
                SPmHelperConfig.get().setToken(responseJson.get("token").getAsString());
                clientPlayer.sendMessage(Text.translatable("text.spmhelper.WebSocketClient_TokenWritten"));
                SPmHelperApi.webSocket.abort();
            } else if (responseJson.has("error")) {
                if (responseJson.get("error").getAsString().equals("Authorization denied by user")) {
                    clientPlayer.sendMessage(Text.translatable("text.spmhelper.WebSocketClient_AuthorizationCancelledByYou"));
                } else {
                    clientPlayer.sendMessage(Text.translatable("text.spmhelper.WebSocketClient_FailedToGetLink"));
                }
                SPmHelperApi.webSocket.abort();
            }
        }
        aggregatedText.setLength(0);
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        //System.out.println("🔴 Соединение закрыто: " + statusCode + " - " + reason);
        latch.countDown();
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        error.printStackTrace();
        latch.countDown(); // Разблокируем основной поток
    }
}