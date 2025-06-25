package zadudoder.spmhelper.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.types.BranchCoords;
import zadudoder.spmhelper.utils.types.HubBranch;


@Mixin(InGameHud.class)
public abstract class GameHudMixin {
    @Final
    @Shadow
    private MinecraftClient client;
    @Unique
    private int X;
    @Unique
    private int Y;

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        /*if (!Misc.isOnAllowedServer() || client.options.hudHidden) {
            return;
        }*/

        if (SPmHelperConfig.get().enableSPmNav && client.player.getWorld().getRegistryKey() == World.NETHER) {
            MatrixStack matrix = context.getMatrices();
            matrix.push();
            float labelSize = SPmHelperConfig.get().SPmNavScale / 100f;

            matrix.scale(labelSize, labelSize, 1);


            int screenWidth = (int) (client.getWindow().getScaledWidth() / labelSize);
            int screenHeight = (int) (client.getWindow().getScaledHeight() / labelSize);

            TextRenderer textRenderer = client.textRenderer;
            BranchCoords branchCoords = Misc.getBranch(client.player.getBlockPos());
            Text branchName = Text.translatable("text.spmhelper.branch_name." + branchCoords.branch.name().toLowerCase());

            if (branchCoords.branch == HubBranch.HUB) {
                X = (int) (screenWidth * (SPmHelperConfig.get().SPmNavX / 100.0) - textRenderer.getWidth(branchName) / 2);
                Y = (int) (screenHeight * (SPmHelperConfig.get().SPmNavY / 100.0));
                X = Math.max(0, Math.min(X, screenWidth - textRenderer.getWidth(branchName) - 4));
                Y = Math.max(0, Math.min(Y, screenHeight - 10));

                drawLabel(context, branchName, branchCoords.getBranchColor());
            } else {
                Text fullText = branchName.copy().append(Text.literal(" | " + branchCoords.pos));
                X = (int) (screenWidth * (SPmHelperConfig.get().SPmNavX / 100.0) - textRenderer.getWidth(fullText) / 2);
                Y = (int) (screenHeight * (SPmHelperConfig.get().SPmNavY / 100.0));
                X = Math.max(0, Math.min(X, screenWidth - textRenderer.getWidth(fullText) - 4));
                Y = Math.max(0, Math.min(Y, screenHeight - 10));
                drawLabel(context, fullText, branchCoords.getBranchColor());
            }
            matrix.pop();
        }
    }

    @Unique
    private void drawLabel(DrawContext context, Text text, int color) {
        TextRenderer textRenderer = client.textRenderer;
        context.fill(X - 2, Y - 4, X + textRenderer.getWidth(text) + 2, Y - 3, SPmHelperConfig.get().SpmNavBackgroundColor);
        context.fill(X - 3, Y - 3, X + textRenderer.getWidth(text) + 3, Y + textRenderer.fontHeight + 1, SPmHelperConfig.get().SpmNavBackgroundColor);
        context.fill(X - 2, Y + textRenderer.fontHeight + 2, X + textRenderer.getWidth(text) + 2, Y + textRenderer.fontHeight + 1, SPmHelperConfig.get().SpmNavBackgroundColor);
        context.drawText(textRenderer, text, X, Y, color, false);
    }
}

