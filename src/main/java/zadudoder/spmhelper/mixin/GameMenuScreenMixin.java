package zadudoder.spmhelper.mixin;


import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends ScreenMixin {


    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo info) {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int startY = this.height / 2;
        int centerX = this.width / 2 - buttonWidth / 2;

        ButtonWidget callsButton = ButtonWidget.builder(
                        Text.literal("Вызовы"),
                        button -> this.client.setScreen(new CallsScreen()))
                .dimensions(centerX - 170, startY - 50, buttonWidth, buttonHeight)
                .build();

        this.addDrawableChild(callsButton);
    }
}
