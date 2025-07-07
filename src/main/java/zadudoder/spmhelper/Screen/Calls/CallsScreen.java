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
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SPmHelperApi;
import zadudoder.spmhelper.utils.types.Service;

public class CallsScreen extends Screen {
    private TextFieldWidget commentField;
    private boolean sendCoordinates = false;
    private BlockPos playerPos;
    private boolean hasToken;
    private ButtonWidget detectiveButton;
    private ButtonWidget fsbButton;
    private ButtonWidget bankerButton;
    private ButtonWidget guideButton;
    private String statusMessage;
    private int statusColor;
    private String errorMessage = null;

    public CallsScreen() {
        super(Text.of("Экран вызова"));
    }

    @Override
    protected void init() {
        super.init();
        this.hasToken = SPmHelperConfig.get().getAPI_TOKEN() != null && !SPmHelperConfig.get().getAPI_TOKEN().isEmpty();
        boolean isOnCorrectServer = Misc.isOnAllowedServer();
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
                Text.translatable("text.spmhelper.calls_TextFieldWidget")
        );
        this.addDrawableChild(commentField);

        // Кнопки вызова
        int buttonY = height / 2 + 40;
        int buttonWidth = 65;


        detectiveButton = ButtonWidget.builder(Text.translatable("text.spmhelper.calls_detective"), (btn) -> {
            Service serviceType = Service.DETECTIVE;
            callService(serviceType);
        }).dimensions(width / 2 - 150, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(detectiveButton);

        fsbButton = ButtonWidget.builder(Text.translatable("text.spmhelper.calls_fsb"), (btn) -> {
            Service serviceType = Service.FSB;
            callService(serviceType);
        }).dimensions(width / 2 - 150 + buttonWidth + 10, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(fsbButton);

        bankerButton = ButtonWidget.builder(Text.translatable("text.spmhelper.calls_banker"), (btn) -> {
            Service serviceType = Service.BANKER;
            callService(serviceType);
        }).dimensions(width / 2 - 150 + 2 * buttonWidth + 25, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(bankerButton);

        guideButton = ButtonWidget.builder(Text.translatable("text.spmhelper.calls_guide"), (btn) -> {
            Service serviceType = Service.GUIDE;
            callService(serviceType);
        }).dimensions(width / 2 - 150 + 3 * buttonWidth + 40, buttonY, buttonWidth, 20).build();
        this.addDrawableChild(guideButton);

        if (SPmHelperApi.getAPIStatus() != 200) {
            errorMessage = "text.spmhelper.calls_errorMessageNot200";
        } else if (!hasToken) {
            errorMessage = "text.spmhelper.calls_errorMessageNotHasToken";
        } else if (!isOnCorrectServer) {
            errorMessage = "text.spmhelper.calls_errorMessageNotIsOnCorrectServer";
        }


        if (!hasToken) {
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.spmhelper.calls_Login"), btn -> {
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
        ButtonWidget Back = ButtonWidget.builder(Text.of("⬅"), (btn) -> this.client.setScreen(new MainScreen())).dimensions(5, 10, 15, 15).build();
        this.addDrawableChild(Back);
    }

    private void updateButtonsState() {
        detectiveButton.active = !detectiveButton.active;
        fsbButton.active = !fsbButton.active;
        bankerButton.active = !bankerButton.active;
        guideButton.active = !guideButton.active;
    }

    public void callService(Service serviceType) {
        if (!hasToken) {
            setStatus(Text.translatable("text.spmhelper.calls_callService_NotHasToken").getString(), 0xFF5555);
            return;
        }

        String comment = commentField.getText().trim();
        if (comment.isEmpty()) {
            setStatus(Text.translatable("text.spmhelper.calls_callService_CommentIsEmpty").getString(), 0xFF5555);
            return;
        }
        String world = switch (MinecraftClient.getInstance().player.getWorld().getRegistryKey().getValue().toString()) {
            case "minecraft:the_nether" -> "Ад";
            case "minecraft:the_end" -> "Энд";
            default -> "Верхний мир";
        };
        String coordinates = sendCoordinates && playerPos != null ?
                "**" + playerPos.getX() + " " + playerPos.getY() + " " + playerPos.getZ() + ' ' + world + "**" : " ";
        setStatus(Text.translatable("text.spmhelper.calls_callService_SendRequest").getString(), 0xFFFF55);
        updateButtonsState();

        String personName = switch (serviceType) {
            case DETECTIVE -> "Детектив";
            case FSB -> "ФСБ";
            case BANKER -> "Банкир";
            case GUIDE -> "Гид";
        };

        SPmHelperApi.makeCall(serviceType, coordinates, comment)
                .thenAccept(success -> MinecraftClient.getInstance().execute(() -> {
                    if (success) {
                        setStatus(String.format(Text.translatable("text.spmhelper.calls_callService_WasCalled").getString(), personName), 0x55FF55);
                        updateButtonsState();
                    } else {
                        setStatus(Text.translatable("text.spmhelper.calls_callService_ErrorSendingCall").getString(), 0xFF5555);
                        detectiveButton.active = true;
                        fsbButton.active = true;
                        bankerButton.active = true;
                        guideButton.active = true;
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

        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.translatable(errorMessage),
                    width / 2,
                    height / 2 - 60,
                    0xFF5555
            );
        }


        // Подписи полей
        context.drawText(textRenderer, Text.translatable("text.spmhelper.calls_render_SendCoordinates"),
                width / 2 - 150, height / 2 - 35, 0xFFFFFF, false);

        if (sendCoordinates && playerPos != null) {
            context.drawText(textRenderer,
                    String.format(Text.translatable("text.spmhelper.calls_render_SendCoordinatesAndPlayerPos").getString(),
                            playerPos.getX(), playerPos.getY(), playerPos.getZ()),
                    width / 2 - 150, height / 2 - 20, 0xFFFFFF, false);
        }

        context.drawText(textRenderer, Text.translatable("text.spmhelper.calls_render_Comment"),
                width / 2 - 150, height / 2 - 5, 0xFFFFFF, false);

        if (commentField.getText().isEmpty()) {
            context.drawText(textRenderer, Text.translatable("text.spmhelper.calls_render_EnterTextHere"),
                    width / 2 - 145, height / 2 + 16, 0xBBBBBB, false);
        }
        if (statusMessage != null) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.of(statusMessage),
                    this.width / 2,
                    this.height / 2 + 65,
                    statusColor
            );
        }

        renderTitle(context);
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