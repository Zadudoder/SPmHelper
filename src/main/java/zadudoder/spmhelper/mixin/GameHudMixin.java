package zadudoder.spmhelper.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
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

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MatrixStack matrix = context.getMatrices();
        matrix.push();
        if (SPmHelperConfig.get().enableSPmNav && client.player.getWorld().getRegistryKey() == World.NETHER) {
            TextRenderer textRenderer = client.textRenderer;
            BranchCoords branchCoords = Misc.getBranch(client.player.getBlockPos());
            Text branchName = Text.translatable("text.spmhelper.branch_name." + branchCoords.branch.name().toLowerCase());
            if(branchCoords.branch == HubBranch.HUB){
                context.drawTextWithShadow(textRenderer, branchName, 2, 2, 0xFFFFFF);
            } else {
                branchName = branchName.copy().append(": ");
                context.drawTextWithShadow(textRenderer, branchName, 2, 2, branchCoords.getBranchColor());
                context.drawTextWithShadow(textRenderer, String.valueOf(branchCoords.pos), 2 + textRenderer.getWidth(branchName), 2, 0xFFFFFF);
            }
        }
        matrix.pop();
    }
}

