package zadudoder.spmhelper.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.utils.Misc;

public class ModEvents {
    public static void registerEvents() {
        registerBlockClickHandler();
    }

    private static void registerBlockClickHandler() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient) {
                if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof SignBlock) {
                    SignBlockEntity signBlockEntity = (SignBlockEntity) world.getBlockEntity(hitResult.getBlockPos());
                    SignText frontText = signBlockEntity.getFrontText();
                    String firstLine = frontText.getMessage(0, false).getString();
                    if (firstLine.contains("#SPmHPay") && signBlockEntity.isWaxed()) {
                        String cardNumber = frontText.getMessage(1, false).getString().replaceAll(" ", "");
                        String amount = frontText.getMessage(2, false).getString().replaceAll(" ", "");
                        String comment = frontText.getMessage(3, false).getString().replaceAll("", "");
                        if (!Misc.isNumeric(cardNumber)) {
                            return ActionResult.PASS;
                        }
                        if (amount.contains("АР")) {
                            amount = amount.replaceFirst("АР", "");
                            if (Misc.isNumeric(cardNumber)) {
                                String finalAmount = amount;
                                MinecraftClient.getInstance().execute(() -> {
                                    MinecraftClient.getInstance().setScreen(new PayScreen(cardNumber, finalAmount, comment));
                                });
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
