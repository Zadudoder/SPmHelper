package zadudoder.spmhelper.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ServerInfo;
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

import static zadudoder.spmhelper.Screen.Calls.CallsScreen.ALLOWED_SERVERS;

@Mixin(InGameHud.class)
public abstract class GameHudMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MatrixStack matrix = context.getMatrices();
        matrix.push();
        ServerInfo serverInfo = client.getCurrentServerEntry();

        if (serverInfo != null && !client.options.hudHidden) {
            String serverAddress = serverInfo.address;
            String domain = serverAddress.split(":")[0];
            boolean onAllowedServer = ALLOWED_SERVERS.stream().anyMatch(allowed -> domain.equals(allowed) || domain.startsWith(allowed + ":"));

        if (SPmHelperConfig.get().enableSPmNav && client.player.getWorld().getRegistryKey() == World.NETHER && onAllowedServer) {
            TextRenderer textRenderer = client.textRenderer;
            BranchCoords branchCoords = Misc.getBranch(client.player.getBlockPos());
            Text branchName = Text.translatable("text.spmhelper.branch_name." + branchCoords.branch.name().toLowerCase());

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            if(branchCoords.branch == HubBranch.HUB) {
                // Для HUB
                int X = (int) (screenWidth * (SPmHelperConfig.get().SPmNavX / 100.0) - textRenderer.getWidth(branchName) / 2);
                int Y = (int) (screenHeight * (SPmHelperConfig.get().SPmNavY / 100.0));
                X = Math.max(0, Math.min(X, screenWidth - textRenderer.getWidth(branchName) - 4));
                Y = Math.max(0, Math.min(Y, screenHeight - 10));

                // Сначала фон, потом текст
                context.fill(X - 4, Y - 4, X + textRenderer.getWidth(branchName) + 4, Y + textRenderer.fontHeight + 2, 0x60000000);
                context.drawTextWithShadow(textRenderer, branchName, X, Y, 0xFFFFFF);
            } else {
                // Для других веток
                Text fullText = branchName.copy().append(Text.literal(" | " + branchCoords.pos));
                int X = (int) (screenWidth * (SPmHelperConfig.get().SPmNavX / 100.0) - textRenderer.getWidth(fullText) / 2);
                int Y = (int) (screenHeight * (SPmHelperConfig.get().SPmNavY / 100.0));
                X = Math.max(0, Math.min(X, screenWidth - textRenderer.getWidth(fullText) - 4));
                Y = Math.max(0, Math.min(Y, screenHeight - 10));

                // Сначала фон, потом текст
                context.fill(X - 4, Y - 4, X + textRenderer.getWidth(fullText) + 4, Y + textRenderer.fontHeight + 2, 0x60000000);
                context.drawTextWithShadow(textRenderer, fullText, X, Y, branchCoords.getBranchColor());
            }
        }
        matrix.pop();
        }
    }
}

