package zadudoder.spmhelper.Screen.Calls;

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
import zadudoder.spmhelper.utils.SPmHelperApi;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CallsScreen extends Screen {
    private static final List<String> ALLOWED_SERVERS = Arrays.asList(
            "spm.spworlds.org",
            "spm.spworlds.ru"
    );

    private TextFieldWidget commentField;
    private CheckboxWidget coordinatesCheckbox;
    private boolean sendCoordinates = false;
    private String lastStatus = "";
    private boolean isSuccess = false;
    private BlockPos playerPos;
    private boolean hasToken;
    private boolean isOnCorrectServer;

    public CallsScreen() {
        super(Text.of("Экран вызова"));
    }

    @Override
    protected void init() {
        super.init();
        this.hasToken = SPmHelperClient.config.getAPI_TOKEN() != null && !SPmHelperClient.config.getAPI_TOKEN().isEmpty();
        isOnCorrectServer = checkServer();
        this.playerPos = MinecraftClient.getInstance().player != null ?
                MinecraftClient.getInstance().player.getBlockPos() : null;

        // Чекбокс для координат
        coordinatesCheckbox = CheckboxWidget.builder(Text.of(""), textRenderer)
                .pos(width / 2 - 10, height / 2 - 40)
                .checked(false)
                .callback((checkbox, checked) -> sendCoordinates = checked)
                .build();
        this.addDrawableChild(coordinatesCheckbox);

        // Поле комментария
        this.commentField = new TextFieldWidget(
                textRenderer,
                width / 2 - 150, height / 2 + 10,
                300, 20,
                Text.of("Введите комментарий для вызова:")
        );
        this.addDrawableChild(commentField);

        // Кнопки вызова
        int buttonY = height / 2 + 40;
        int buttonWidth = 90;

        this.addDrawableChild(createServiceButton("Детектив", "detective", "Детектив",
                width / 2 - 150, buttonY, buttonWidth));
        this.addDrawableChild(createServiceButton("ФСБ", "fsb", "ФСБ",
                width / 2 - 150 + buttonWidth + 15, buttonY, buttonWidth));
        this.addDrawableChild(createServiceButton("Банкир", "banker", "Банкир",
                width / 2 - 150 + 2 * buttonWidth + 30, buttonY, buttonWidth));

        // Кнопка группы
        this.addDrawableChild(ButtonWidget.builder(Text.of("✈"), btn ->
                        Util.getOperatingSystem().open("https://spworlds.ru/spm/groups/06c25d05-b370-47d4-8416-fa1011ea69a1"))
                .dimensions(width - 20, 10, 15, 15)
                .build());
    }

    private boolean checkServer() {
        if (MinecraftClient.getInstance().getCurrentServerEntry() == null) {
            return false;
        }
        String address = MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase();
        return ALLOWED_SERVERS.stream().anyMatch(allowed ->
                address.equals(allowed) ||
                        address.startsWith(allowed + ":") ||
                        address.endsWith("." + allowed));
    }

    private ButtonWidget createServiceButton(String text, String serviceType, String personName, int x, int y, int width) {
        return ButtonWidget.builder(Text.of(text), button -> callService(serviceType, personName))
                .dimensions(x, y, width, 20)
                .build();
    }

    public void callService(String serviceType, String personName) {
        String comment = commentField.getText().trim();
        if (comment.isEmpty() && !sendCoordinates) {
            setStatus("Ошибка: введите комментарий!", false);
            return;
        }

        String coordinates = sendCoordinates && playerPos != null ?
                "**"+playerPos.getX() + " " + playerPos.getY() + " " + playerPos.getZ() + MinecraftClient.getInstance().player.getWorld().getRegistryKey() + "**" : "";

        setStatus("Отправка запроса...", false);

        SPmHelperApi.makeCall(serviceType, coordinates, comment)
                .thenAccept(success -> {
                    MinecraftClient.getInstance().execute(() -> {
                        if (success) {
                            setStatus(personName + " был вызван!", true);
                        } else {
                            setStatus("Ошибка отправки вызова", false);
                        }
                    });
                });
    }

    private void setStatus(String message, boolean success) {
        this.lastStatus = message;
        this.isSuccess = success;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        //coordinatesCheckbox.active = isOnCorrectServer;

        // Статус токена и сервера
        if (!hasToken) {
            drawCenteredText(context, "⚠ Сначала авторизуйтесь (/spmhelper auth)",
                    width / 2, height / 2 - 60, 0xFF5555);
        } else if (!isOnCorrectServer) {
            drawCenteredText(context, "❗ Координаты можно вводить только на СПм",
                    width / 2, height / 2 - 60, 0xFFFF55);
        }

        // Подписи полей
        context.drawText(textRenderer, "Отправить координаты:",
                width / 2 - 150, height / 2 - 35, 0xFFFFFF, false);

        if (sendCoordinates && playerPos != null) {
            context.drawText(textRenderer,
                    String.format("Координаты: X:%d Y:%d Z:%d", playerPos.getX(), playerPos.getY(), playerPos.getZ()),
                    width / 2 - 150, height / 2 - 20, 0xFFFFFF, false);
        }

        context.drawText(textRenderer, "Комментарий:",
                width / 2 - 150, height / 2 - 5, 0xFFFFFF, false);

        if (commentField.getText().isEmpty()) {
            context.drawText(textRenderer, "Введите сюда текст",
                    width / 2 - 145, height / 2 + 16, 0xBBBBBB, false);
        }

        // Статус операции
        if (!lastStatus.isEmpty()) {
            drawCenteredText(context, lastStatus,
                    width / 2, height / 2 + 70, isSuccess ? 0x55FF55 : 0xFFFF55);
        }

        // Заголовок
        renderTitle(context);
    }

    private void drawCenteredText(DrawContext context, String text, int x, int y, int color) {
        context.drawText(textRenderer, text,
                x - textRenderer.getWidth(text) / 2, y, color, false);
    }

    private void renderTitle(DrawContext context) {
        Identifier texture = Identifier.of("spmhelper", "titles/callstextrender.png");
        int imageWidth = 704 / 4;
        int imageHeight = 152 / 4;
        int availableWidth = width - 40;

        if (imageWidth > availableWidth) {
            float scale = (float) availableWidth / imageWidth;
            imageWidth = availableWidth;
            imageHeight = (int) (imageHeight * scale);
        }

        int x = (width - imageWidth) / 2;
        int y = height / 2 - 110;

        context.drawTexture(texture, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}