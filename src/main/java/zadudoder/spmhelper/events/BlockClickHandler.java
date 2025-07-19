package zadudoder.spmhelper.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import zadudoder.spmhelper.Screen.Calls.ServiceAcceptScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.types.Service;

public class BlockClickHandler {

    public static void registerBlockClickHandler() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof AbstractSignBlock) {
                SignBlockEntity signBlockEntity = (SignBlockEntity) world.getBlockEntity(hitResult.getBlockPos());
                if (signBlockEntity != null && signBlockEntity.isWaxed()) {
                    SignText frontText = signBlockEntity.getFrontText();
                    String firstLine = frontText.getMessage(0, false).getString();
                    if (firstLine.contains("#SPmHPay")) {
                        String cardNumber = frontText.getMessage(1, false).getString().replaceAll(" ", "");
                        String amount = frontText.getMessage(2, false).getString().replaceAll(" ", "");
                        String comment = frontText.getMessage(3, false).getString().replaceAll("", "");
                        if (!Misc.isNumeric(cardNumber)) {
                            return ActionResult.PASS;
                        }
                        if (amount.contains("АР")) {
                            amount = amount.replaceFirst("АР", "");
                        }
                        if (Misc.isNumeric(amount)) {
                            String finalAmount = amount;
                            MinecraftClient.getInstance().
                                    execute(() -> MinecraftClient.getInstance().setScreen(new PayScreen(cardNumber, finalAmount, comment)));
                            return ActionResult.SUCCESS;
                        } else if (amount.isEmpty()) {
                            MinecraftClient.getInstance().
                                    execute(() -> MinecraftClient.getInstance().setScreen(new PayScreen(cardNumber)));
                            return ActionResult.SUCCESS;
                        }
                    } else if (firstLine.contains("#SPmHCall")) {
                        Service service = null;
                        String secondLine = frontText.getMessage(1, false).getString().replaceAll(" ", "").toLowerCase();
                        switch (secondLine) {
                            case "детектив", "детектива" -> service = Service.DETECTIVE;
                            case "фсб" -> service = Service.FSB;
                            case "банкир", "банкира" -> service = Service.BANKER;
                            case "гид", "гида" -> service = Service.GUIDE;
                        }
                        if (service == null) {
                            return ActionResult.PASS;
                        } else {
                            String comment = frontText.getMessage(2, false).getString() + ' ' + frontText.getMessage(3, false).getString();
                            Service finalService = service;
                            MinecraftClient.getInstance().
                                    execute(() -> MinecraftClient.getInstance().setScreen(new ServiceAcceptScreen(finalService, comment, player)));
                            return ActionResult.SUCCESS;
                        }
                    } else if (firstLine.toLowerCase().contains("оплата по карте")) {
                        String secondLine = frontText.getMessage(1, false).getString();
                        if (Misc.isNumeric(secondLine)) {
                            MinecraftClient.getInstance().
                                    execute(() -> MinecraftClient.getInstance().setScreen(new PayScreen(secondLine)));
                            return ActionResult.SUCCESS;
                        }
                        return ActionResult.PASS;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

}
