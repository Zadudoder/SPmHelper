package zadudoder.spmhelper.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SPmHelperApi;

import static zadudoder.spmhelper.utils.SPmHelperApi.startAuthProcess;

@Environment(EnvType.CLIENT)
public class ModEvents {
    public static void registerEvents() {
        registerBlockClickHandler();
        registerCommands();
    }

    private static void registerBlockClickHandler() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof AbstractSignBlock) {
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
                            MinecraftClient.getInstance().
                                    execute(() -> {
                                        MinecraftClient.getInstance().setScreen(new PayScreen(cardNumber, finalAmount, comment));
                                    });
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Основная команда spmhelper
            dispatcher.register(
                    ClientCommandManager.literal("spmhelper")
                            .then(ClientCommandManager.literal("auth")
                                    .executes(context -> {
                                        startAuthProcess(context.getSource());
                                        return 1;
                                    })
                            )
                            .then(ClientCommandManager.literal("status")
                                    .executes(context -> {
                                        SPmHelperApi.getAuthStatus().thenAccept(status -> {
                                            String message = switch (status) {
                                                case 200 -> "§aТокен действителен";
                                                case 401 -> "§cТокен недействителен";
                                                default -> "§cОшибка API: " + status;
                                            };
                                            context.getSource().sendFeedback(Text.literal(message));
                                        });
                                        return 1;
                                    })
                            )
            );

            dispatcher.register(
                    ClientCommandManager.literal("spmh")
                            .then(ClientCommandManager.literal("auth")
                                    .executes(context -> dispatcher.execute("spmhelper auth", context.getSource()))
                            )
                            .then(ClientCommandManager.literal("status")
                                    .executes(context -> dispatcher.execute("spmhelper status", context.getSource()))
                            )
            );
        });
    }
}
