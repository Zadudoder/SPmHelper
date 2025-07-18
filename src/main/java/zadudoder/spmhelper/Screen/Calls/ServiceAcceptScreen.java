package zadudoder.spmhelper.Screen.Calls;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SPmHelperApi;
import zadudoder.spmhelper.utils.types.Service;

public class ServiceAcceptScreen extends Screen {
    private final Service service;
    private final String comment;
    private final PlayerEntity sender;
    private boolean sendCoordinates = false;
    private BlockPos playerPos;

    public ServiceAcceptScreen(Service service, String comment, PlayerEntity sender) {
        super(Text.of("Экран подтверждения вызова"));
        this.service = service;
        this.comment = comment;
        this.sender = sender;
    }

    protected void init() {
        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spmhelper.ru");
        }).dimensions(width - 20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);

        ButtonWidget Back = ButtonWidget.builder(Text.of("❌"), (btn) -> {
            this.close();
        }).dimensions(5, 10, 15, 15).build();
        this.addDrawableChild(Back);

        CheckboxWidget coordinatesCheckbox = CheckboxWidget.builder(Text.of(Text.translatable("text.spmhelper.SAScalls_coordinatesCheckbox").getString()), textRenderer)
                .pos(width / 2 - 10, height / 2 - 10)
                .checked(false)
                .callback((checkbox, checked) -> sendCoordinates = checked)
                .build();
        this.addDrawableChild(coordinatesCheckbox);

        boolean isOnCorrectServer = Misc.isOnAllowedServer();
        coordinatesCheckbox.active = isOnCorrectServer;

        this.playerPos = MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getBlockPos() : null;

        ButtonWidget AcceptButton = ButtonWidget.builder(Text.translatable("text.spmhelper.addCard_AcceptButton"), (btn) -> {
            String world = Misc.getWorldName(sender.getWorld());
            String coordinates = sendCoordinates && playerPos != null ? "**" + sender.getBlockPos().getX() + " " + sender.getBlockPos().getY() + " " + sender.getBlockPos().getZ() + ' ' + world + "**"  : " ";
            SPmHelperApi.makeCall(service, coordinates, comment)
                    .thenAccept(success -> MinecraftClient.getInstance().execute(() -> {
                        if (success) {
                            sender.sendMessage(Text.of(String.format(Text.translatable("text.spmhelper.calls_callService_WasCalled").getString(), service.name())));
                        } else {
                            sender.sendMessage(Text.of(Text.translatable("text.spmhelper.calls_callService_ErrorSendingCall").getString()));
                        }
                        this.close();
                    }));
            this.close();
        }).dimensions(width / 2 - 120, height / 2, 80, 20).build();
        this.addDrawableChild(AcceptButton);

        ButtonWidget DeclineButton = ButtonWidget.builder(Text.translatable("text.spmhelper.addCard_DeclineButton"), (btn) -> {
            this.close();
        }).dimensions(width / 2 + 40, height / 2, 80, 20).build();
        this.addDrawableChild(DeclineButton);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.spmhelper.SAScalls_coordinatesCheckbox"),
                this.width / 2,
                this.height / 2 - 20,
                0xFFFFFF
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.spmhelper.DoYouWantCall_" + service.name().toLowerCase()),
                this.width / 2,
                this.height / 2 - 40,
                0xFFFFFF
        );

    }
}

