package zadudoder.spmhelper.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
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

import java.util.ArrayList;
import java.util.List;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends ScreenMixin {
    private ButtonWidget menuButton;

    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo info) {
        if (SPmHelperConfig.get().enableMenuButton) {
            int buttonWidth = 20;
            int buttonHeight = 20;

            for (Element widget : ((GameMenuScreen)(Object)this).children()) {
                if (widget instanceof ButtonWidget button &&
                        button.getMessage().getString().equals("Моды")) {

                    int buttonX = button.getX() - buttonWidth - 4;
                    int buttonY = button.getY();

                    this.menuButton = ButtonWidget.builder(
                                    Text.literal("\uD83C\uDFB2"),
                                    btn -> openSelectedScreen())
                            .dimensions(buttonX, buttonY, buttonWidth, buttonHeight)
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

        Screen screenToOpen;
        switch (SPmHelperConfig.get().defaultScreen) {
            case Настройки:
                screenToOpen = new Settings();
                break;
            case Оплата:
                screenToOpen = new PayScreen();
                break;
            case Вызовы:
                screenToOpen = new CallsScreen();
                break;
            case Карта:
                screenToOpen = new MapScreen();
                break;
            case Законы:
                screenToOpen = new LawsScreen();
                break;
            case SPmHelper:
            default:
                screenToOpen = new MainScreen();
        }

        client.setScreen(screenToOpen);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (menuButton != null && menuButton.visible) {
            // Tooltip
            if (menuButton.isSelected() && client != null) {
                Text tooltipText = Text.translatable("text.spmhelper.current_screen",
                        SPmHelperConfig.get().defaultScreen.getTranslatedName());

                context.drawTooltip(
                        client.textRenderer,
                        tooltipText,
                        mouseX, mouseY
                );
            }
        }
    }
}