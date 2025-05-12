package zadudoder.spmhelper.Screen.Calls;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import zadudoder.spmhelper.SPmHelperClient;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static zadudoder.spmhelper.URL.SERVER_URL;

public class CallsScreen extends Screen {

    private TextFieldWidget commentField;
    private boolean sendCoordinates = false;
    private String lastStatus = "";
    private boolean isSuccess = false;
    private BlockPos playerPos;
    private int startY;
    private boolean hasToken;
    private boolean isOnCorrectServer;


    private static final List<String> ALLOWED_SERVERS = Arrays.asList(
            "spm.spworlds.org",
            "spm.spworlds.ru"
    );

    public CallsScreen() {
        super(Text.of("Экран вызова"));
    }

    @Override
    protected void init() {
        super.init();

        // Проверяем наличие токена при инициализации экрана
        hasToken = SPmHelperClient.config.getTOKEN() != null && SPmHelperClient.config.getID() != null;
        isOnCorrectServer = checkServer();

        if (client != null && client.player != null) {
            playerPos = client.player.getBlockPos();
        }

        CheckboxWidget coordinatesCheckbox = CheckboxWidget.builder(Text.of(""), textRenderer)
                .pos(width / 2 - 150 + textRenderer.getWidth("Отправить координаты:") + 10, height / 2 - 80)
                .checked(false) // Начальное состояние
                .callback((checkbox, checked) -> sendCoordinates = checked)
                .build();
        this.addDrawableChild(coordinatesCheckbox);

        // Поле для комментария
        commentField = new TextFieldWidget(
                textRenderer,
                width / 2 - 150, height / 2 - 30,
                300, 20,
                Text.of("Введите комментарий для вызова:")
        );
        this.addDrawableChild(commentField);

        // Кнопки вызова
        int buttonY = height / 2 + 10;
        int buttonWidth = 90;

        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spworlds.ru/spm/groups/06c25d05-b370-47d4-8416-fa1011ea69a1");
        }).dimensions(width-20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);

        this.addDrawableChild(
                createServiceButton("Детектив", "detective", "детектива",
                        width / 2 - 150, buttonY, buttonWidth)
        );

        this.addDrawableChild(
                createServiceButton("ФСБ", "fsb", "ФСБ",
                        width / 2 - 150 + buttonWidth + 15, buttonY, buttonWidth)
        );

        this.addDrawableChild(
                createServiceButton("Банкир", "banker", "банкира",
                        width / 2 - 150 + 2*buttonWidth + 30, buttonY, buttonWidth)
        );
    }

    private boolean checkServer() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null || client.getCurrentServerEntry() == null) {
            return false;
        }

        String serverAddress = client.getCurrentServerEntry().address.toLowerCase();

        // Проверяем все разрешенные адреса
        for (String allowed : ALLOWED_SERVERS) {
            if (serverAddress.equals(allowed) ||
                    serverAddress.startsWith(allowed + ":") ||
                    serverAddress.endsWith("." + allowed)) {
                return true;
            }
        }

        return false;
    }

    private ButtonWidget createServiceButton(String buttonText, String serviceType, String personName, int x, int y, int width) {
        ButtonWidget ServiceButton = ButtonWidget.builder(Text.of(buttonText), button -> {
                    if (!hasToken) {
                        lastStatus = "Ошибка: сначала привяжите карту (/spmhelper)";
                        isSuccess = false;
                        return;
                    }
                    if (!isOnCorrectServer) {
                        lastStatus = "Ошибка: доступно только на сервере СПм";
                        isSuccess = false;
                        return;

                    }
                    callPerson(serviceType, personName);
                })
                .dimensions(x, y, width, 20)
                .build();
        ServiceButton.active = hasToken && isOnCorrectServer;
        return ServiceButton;
    }

    private void callPerson(String serviceType, String personName) {
        String comment = commentField.getText().trim();
        if (comment.isEmpty() && !sendCoordinates) {
            lastStatus = "Ошибка: введите комментарий!";
            isSuccess = false;
            return;

        }

        String playerName = client != null && client.player != null
                ? client.player.getName().getString()
                : "Неизвестный игрок";

        String coordinates = sendCoordinates && playerPos != null
                ? playerPos.getX() + " " + playerPos.getY() + " " + playerPos.getZ()
                : "";

        String message = playerName + " вызывает " + personName + ": " + comment;

        new Thread(() -> {
            try {
                boolean success = sendToServer(serviceType, message, coordinates);
                lastStatus = success ? "Успешно отправлено!" : "Ошибка отправки!";
                isSuccess = success;
            } catch (Exception e) {
                lastStatus = "Ошибка: " + e.getMessage();
                isSuccess = false;
            }
        }).start();
    }

    private boolean sendToServer(String serviceType, String message, String coordinates) throws Exception {
        String cardId = SPmHelperClient.config.getID();
        String token = SPmHelperClient.config.getTOKEN();

        if (cardId == null || token == null) {
            throw new Exception("Карта не привязана");
        }

        URL url = new URL(SERVER_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JsonObject payload = new JsonObject();
        payload.addProperty("cardId", cardId);
        payload.addProperty("token", token);
        payload.addProperty("service", serviceType);
        payload.addProperty("message", message + (coordinates.isEmpty() ? "" : "\n" + coordinates));

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        return responseCode == 200;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Показываем статус привязки карты
        if (!hasToken) {
            context.drawText(
                    textRenderer,
                    "⚠ Сначала привяжите карту (/spmhelper)",
                    width / 2 - textRenderer.getWidth("⚠ Сначала привяжите карту (/spmhelper)")/2,
                    height / 2 - 95,
                    0xFF5555,
                    true
            );
        }
        else if (!isOnCorrectServer) {
            context.drawText(textRenderer,
                    "⚠ Доступно только на сервере СПм",
                    width / 2 - textRenderer.getWidth("⚠ Доступно только на сервере СПм")/2,
                    height / 2 - 95,
                    0xFF5555,
                    true
            );
        }

        // Остальной рендеринг...
        context.drawText(
                textRenderer,
                "Отправить координаты:",
                width / 2 - 150,
                height / 2 - 75,
                0xFFFFFF,
                false
        );

        if (sendCoordinates && playerPos != null) {
            context.drawText(
                    textRenderer,
                    "Координаты: X:" + playerPos.getX() + " Y:" + playerPos.getY() + " Z:" + playerPos.getZ(),
                    width / 2 - 150,
                    height / 2 - 60,
                    0xFFFFFF,
                    false
            );
        }

        context.drawText(
                textRenderer,
                "Комментарий:",
                width / 2 - 150,
                height / 2 - 45,
                0xFFFFFF,
                false
        );

        if (!lastStatus.isEmpty()) {
            int color = isSuccess ? 0x55FF55 : 0xFFFF55;
            context.drawText(
                    textRenderer,
                    lastStatus,
                    width / 2 - textRenderer.getWidth(lastStatus)/2,
                    height / 2 + 40,
                    color,
                    true
            );
        }



        Identifier CallsText = Identifier.of("spmhelper", "titles/callstextrender.png");
        int imageY = height / 2 - 180;
        int originalWidth = 704/2;
        int originalHeight = 152/2;
        int availableWidth = width - 40;
        int finalWidth = originalWidth;
        int finalHeight = originalHeight;
        if (originalWidth > availableWidth) {
            float scale = (float)availableWidth / originalWidth;
            finalWidth = availableWidth;
            finalHeight = (int)(originalHeight * scale);
        }
        int imageX = (width - finalWidth) / 2;
        context.drawTexture(CallsText, imageX, imageY, 0, 0, finalWidth, finalHeight, finalWidth, finalHeight);
    }
}