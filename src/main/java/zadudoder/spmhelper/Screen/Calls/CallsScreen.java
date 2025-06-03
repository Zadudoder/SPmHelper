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
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.SPmHelperApi;

import java.util.Arrays;
import java.util.List;

public class CallsScreen extends Screen {
    public static final List<String> ALLOWED_SERVERS = Arrays.asList(
            "spm.spworlds.org",
            "spm.spworlds.ru"
    );
    private TextFieldWidget commentField;
    private boolean sendCoordinates = false;
    private BlockPos playerPos;
    private boolean hasToken;
    private boolean isOnCorrectServer;
    private ButtonWidget detectiveButton;
    private ButtonWidget fsbButton;
    private ButtonWidget bankerButton;
    private ButtonWidget guideButton;
    private boolean callsActive = true;
    private String statusMessage;
    private int statusColor;

    public CallsScreen() {
        super(Text.of("Экран вызова"));
    }

    @Override
    protected void init() {
        super.init();
        this.hasToken = SPmHelperConfig.get().getAPI_TOKEN() != null && !SPmHelperConfig.get().getAPI_TOKEN().isEmpty();
        isOnCorrectServer = checkServer();
        this.playerPos = MinecraftClient.getInstance().player != null ?
                MinecraftClient.getInstance().player.getBlockPos() : null;

        // Чекбокс для координат
        CheckboxWidget coordinatesCheckbox = CheckboxWidget.builder(Text.of(""), textRenderer)
                .pos(width / 2 - 10, height / 2 - 40)
                .checked(false)
                .callback((checkbox, checked) -> sendCoordinates = checked)
                .build();
        this.addDrawableChild(coordinatesCheckbox);

        coordinatesCheckbox.active = isOnCorrectServer;

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
        int buttonWidth = 65;


        detectiveButton = ButtonWidget.builder(Text.of("Детектив"), (btn) -> {
            String serviceType = "detective";
            String personName = "Детектив";
            callService(serviceType, personName);
        }).dimensions(width / 2 - 150, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(detectiveButton);

        fsbButton = ButtonWidget.builder(Text.of("ФСБ"), (btn) -> {
            String serviceType = "fsb";
            String personName = "ФСБ";
            callService(serviceType, personName);
        }).dimensions(width / 2 - 150 + buttonWidth + 10, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(fsbButton);

        bankerButton = ButtonWidget.builder(Text.of("Банкир"), (btn) -> {
            String serviceType = "banker";
            String personName = "Банкир";
            callService(serviceType, personName);
        }).dimensions(width / 2 - 150 + 2 * buttonWidth + 25, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(bankerButton);

        guideButton = ButtonWidget.builder(Text.of("Гид"), (btn) -> {
            String serviceType = "guide";
            String personName = "Гид";
            callService(serviceType, personName);
        }).dimensions(width / 2 - 150 + 3 * buttonWidth + 40, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(guideButton);


        if (!hasToken) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("Авторизоваться"), btn -> {
                        SPmHelperApi.startAuthProcess(MinecraftClient.getInstance().player);
                        this.close();
                    })
                    .dimensions(width / 2 - 50, height / 2 + 80, 100, 20)
                    .build());
        }

        // Кнопка группы
        this.addDrawableChild(ButtonWidget.builder(Text.of("✈"), btn ->
                        Util.getOperatingSystem().open("https://spmhelper.ru"))
                .dimensions(width - 20, 10, 15, 15)
                .build());
        ButtonWidget Back = ButtonWidget.builder(Text.of("⬅"), (btn) -> {
            this.client.setScreen(new MainScreen());
        }).dimensions(5, 10, 15, 15).build();
        this.addDrawableChild(Back);
    }

    private boolean checkServer() {
        if (MinecraftClient.getInstance().getCurrentServerEntry() == null) {
            return false;
        }
        String address = MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase();
        return ALLOWED_SERVERS.stream().anyMatch(allowed ->
                address.equals(allowed) ||
                        address.startsWith(allowed + ":"));
    }

    private ButtonWidget createServiceButton(String text, String serviceType, String personName, int x, int y, int width) {
        return ButtonWidget.builder(Text.of(text), button -> callService(serviceType, personName))
                .dimensions(x, y, width, 20)
                .build();
    }

    private void updateButtonsState() {
        detectiveButton.active = !detectiveButton.active;
        fsbButton.active = !fsbButton.active;
        bankerButton.active = !bankerButton.active;
        guideButton.active = !guideButton.active;
    }

    public void callService(String serviceType, String personName) {
        if (!hasToken) {
            setStatus("⬇ Сначала авторизуйтесь ⬇", 0xFF5555);
            return;
        }

        String comment = commentField.getText().trim();
        if (comment.isEmpty()) {
            setStatus("Ошибка: введите комментарий!", 0xFF5555);
            return;
        }
        String world = "Верхний мир";
        switch (MinecraftClient.getInstance().player.getWorld().getRegistryKey().getValue().toString()) {
            case "minecraft:overworld":
                world = "Верхний мир";
                break;
            case "minecraft:the_nether":
                world = "Ад";
                break;
            case "minecraft:the_end":
                world = "Энд";
                break;
        }
        String coordinates = sendCoordinates && playerPos != null ?
                "**" + playerPos.getX() + " " + playerPos.getY() + " " + playerPos.getZ() + ' ' + world + "**" : " ";
        setStatus("Отправка запроса...", 0xFFFF55);
        updateButtonsState();


        SPmHelperApi.makeCall(serviceType, coordinates, comment)
                .thenAccept(success -> MinecraftClient.getInstance().execute(() -> {
                    if (success) {
                        setStatus(personName + " был вызван!", 0x55FF55);
                        updateButtonsState();
                    } else {
                        setStatus("Ошибка отправки вызова", 0xFF5555);
                        callsActive = true;
                        detectiveButton.active = callsActive;
                        fsbButton.active = callsActive;
                        bankerButton.active = callsActive;
                        guideButton.active = callsActive;
                    }
                }));
    }

    private void setStatus(String message, int color) {
        this.statusMessage = message;
        this.statusColor = color;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        /*if (SPmHelperApi.getAPIStatus() != 200) {
            drawCenteredText(context, "❗ Ошибка API, обратитесь в тех. поддержку ❗",
                    width / 2, height / 2 - 60, 0xFF5555);
        } else*/ if (!hasToken) {
            drawCenteredText(context, "⬇ Сначала авторизуйтесь ⬇",
                    width / 2, height / 2 - 60, 0xFFFF55);
        } else if (!isOnCorrectServer) {
            drawCenteredText(context, "❗ Координаты указать можно только на сервере СПм ❗",
                    width / 2, height / 2 - 60, 0xFF5555);

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

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of(statusMessage),
                this.width / 2,
                this.height / 2 + 65,
                statusColor
        );

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