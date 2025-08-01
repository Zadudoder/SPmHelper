package zadudoder.spmhelper.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
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
        if (!SPmHelperConfig.get().enableMenuButton) return;

        int buttonWidth = 20;
        int buttonHeight = 20;

        for (Element widget : ((GameMenuScreen) (Object) this).children()) {
            if (widget instanceof ButtonWidget button) {
                if (isModsButton(button)) {
                    createMenuButton(button, buttonWidth, buttonHeight);
                    break;
                }
            }
        }
    }

    @Unique
    private boolean isModsButton(ButtonWidget button) {
        Text buttonText = button.getMessage();
        if (buttonText.getContent() instanceof TranslatableTextContent translatableText) {
            return translatableText.getKey().equals("menu.modded");
        }
        // Резервная проверка для нестандартных серверов
        String text = buttonText.getString();
        return text.equals("Моды") || text.equals("Mods");
    }

    @Unique
    private void createMenuButton(ButtonWidget referenceButton, int width, int height) {
        int buttonX = referenceButton.getX() - width - 4;
        int buttonY = referenceButton.getY();

        Text tooltipText = Text.translatable("text.spmhelper.current_screen")
                .append(Text.translatable("text.spmhelper.screen_type."
                        + SPmHelperConfig.get().defaultScreen.name().toLowerCase()));

        this.menuButton = ButtonWidget.builder(
                        Text.literal(""),
                        openSelectedScreen())
                .dimensions(buttonX, buttonY, width, height)
                .tooltip(Tooltip.of(tooltipText))
                .build();

        this.addDrawableChild(menuButton);
    }

    @Unique
    private ButtonWidget.PressAction openSelectedScreen() {
        if (this.client == null) return null;

        Screen screenToOpen = switch (SPmHelperConfig.get().defaultScreen) {
            case SETTINGS -> new Settings();
            case PAY -> new PayScreen();
            case CALLS -> new CallsScreen();
            case MAP -> new MapScreen();
            case LAWS -> new LawsScreen();
            default -> new MainScreen();
        };

        this.client.setScreen(screenToOpen);
        return null;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!SPmHelperConfig.get().enableMenuButton || this.menuButton == null) return;

        int iconSize = 16;
        int x = menuButton.getX() + (menuButton.getWidth() - iconSize) / 2;
        int y = menuButton.getY() + (menuButton.getHeight() - iconSize) / 2;

        context.drawTexture(
                Identifier.of("spmhelper", "gui/bookwithfeather.png"),
                x, y,
                0, 0,
                iconSize, iconSize,
                iconSize, iconSize
        );
    }
}