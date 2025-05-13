package zadudoder.spmhelper.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class ModEvents {
    public static void registerEvents() {
        registerBlockClickHandler();
    }

    private static void registerBlockClickHandler() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient()) {
                if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof AbstractSignBlock) {
                    player.sendMessage(Text.literal("Вы кликнули по табличке!"), false);
                    return ActionResult.PASS;
                }
            }
            return ActionResult.PASS;
        });
    }
}
