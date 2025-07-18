package zadudoder.spmhelper.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Laws.LawsScreen;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.Screen.Map.MapScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.Screen.Settings;
import zadudoder.spmhelper.config.SPmHelperConfig;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends ScreenMixin {
    @Unique
    private ButtonWidget menuButton;

    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo info) {
        if (SPmHelperConfig.get().enableMenuButton) {
            int buttonWidth = 20;
            int buttonHeight = 20;

            for (Element widget : ((GameMenuScreen) (Object) this).children()) {
                if (widget instanceof ButtonWidget button &&
                        (button.getMessage().getString().equals("Моды") || button.getMessage().getString().equals("Mods"))) {

                    int buttonX = button.getX() - buttonWidth - 4;
                    int buttonY = button.getY();

                    Text tooltipText = Text.translatable("text.spmhelper.current_screen")
                            .append(Text.translatable("text.spmhelper.screen_type." + SPmHelperConfig.get().defaultScreen.name().toLowerCase()));

                    this.menuButton = ButtonWidget.builder(
                                    Text.literal(""),
                                    btn -> openSelectedScreen())
                            .dimensions(buttonX, buttonY, buttonWidth, buttonHeight)
                            .tooltip(Tooltip.of(tooltipText))
                            .build();

                    this.addDrawableChild(menuButton);
                    break;
                }
            }
        }
    }

    @Unique
    private void openSelectedScreen() {
        if (client == null) return;

        Screen screenToOpen = switch (SPmHelperConfig.get().defaultScreen) {
            case SETTINGS -> new Settings();
            case PAY -> new PayScreen();
            case CALLS -> new CallsScreen();
            case MAP -> new MapScreen();
            case LAWS -> new LawsScreen();
            default -> new MainScreen();
        };

        client.setScreen(screenToOpen);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int iconSize = 16;
        int x = menuButton.getX() + (menuButton.getWidth() - iconSize) / 2;
        int y = menuButton.getY() + (menuButton.getHeight() - iconSize) / 2;
        Identifier BUTTON_ICON = Identifier.of("spmhelper", "gui/bookwithfeather.png");
        context.drawTexture(
                BUTTON_ICON,
                x, y,
                0, 0,
                iconSize, iconSize,
                iconSize, iconSize
        );
    }
}