package zadudoder.spmhelper.events;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import zadudoder.spmhelper.SPmHelper;
import zadudoder.spmhelper.Screen.Calls.ServiceAcceptScreen;
import zadudoder.spmhelper.Screen.Pays.AddCardScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SPmHelperApi;
import zadudoder.spmhelper.utils.types.Service;

@Environment(EnvType.CLIENT)
public class ModEvents {
    private static final boolean hasToken = SPmHelperConfig.get().getAPI_TOKEN() != null && !SPmHelperConfig.get().getAPI_TOKEN().isEmpty();

    public static void registerEvents() {
        registerBlockClickHandler();
        registerChatEventHandler();
        registerCommands();
    }

    private static void registerBlockClickHandler() {
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

    private static void registerChatEventHandler() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().contains("Управление картой [Копир. токен] [Копир. айди]") && message.getSiblings().get(0).getSiblings().get(0).getStyle().getClickEvent().getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                String token = message.getSiblings().get(0).getSiblings().get(0).getStyle().getClickEvent().getValue();
                String id = message.getSiblings().get(0).getSiblings().get(1).getStyle().getClickEvent().getValue();
                String name = message.getString().substring(1);
                name = name.substring(0, name.indexOf(']'));
                //System.out.println(name);
                String finalName = name;
                MinecraftClient.getInstance().
                        execute(() -> MinecraftClient.getInstance().setScreen(new AddCardScreen(id, token, finalName)));
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.execute(() -> {
                if (Misc.isOnAllowedServer()) {
                    String clientVersion = FabricLoader.getInstance().getModContainer(SPmHelper.MOD_ID).get().getMetadata().getVersion().toString();
                    String lastVersion = SPmHelperApi.getLastModVersionInfo().get("version_number").getAsString();

                    if (!clientVersion.equals(lastVersion)) {
                        client.player.sendMessage(
                                Text.translatable("text.spmhelper.updateMod_message_firstPart")
                                        .formatted(Formatting.GREEN)
                                        .styled(style -> style.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/spmhelper/version/" + lastVersion)
                                        ))
                                        .append(Text.literal(clientVersion).formatted(Formatting.YELLOW))
                                        .append(Text.translatable("text.spmhelper.updateMod_message_betweenPart"))
                                        .append(Text.literal(lastVersion).formatted(Formatting.GREEN))
                                        .append(Text.translatable("text.spmhelper.updateMod_message_lastPart"))
                        );
                    }
                }

            });
        });
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Главная команда
            var mainCommand = ClientCommandManager.literal("spmhelper")
                    .then(ClientCommandManager.literal("auth")
                            .executes(context -> {
                                if (!hasToken) {
                                    SPmHelperApi.startAuthProcess(context.getSource().getPlayer());
                                    return 1;
                                }
                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.auth_FeedBackMessage"));
                                return 0;
                            })
                    )
                    .then(ClientCommandManager.literal("status")
                            .executes(context -> {
                                SPmHelperApi.getAuthStatus().thenAccept(status -> {
                                    String message = switch (status) {
                                        case 200 -> "text.spmhelper.status_FeedBackMessageCase200";
                                        case 401 -> "text.spmhelper.status_FeedBackMessageCase401";
                                        default -> "text.spmhelper.status_FeedBackMessageCaseDefault" + status;
                                    };
                                    context.getSource().sendFeedback(Text.translatable(message));
                                });
                                return 1;
                            })
                    );

            var aliasMainCommand = ClientCommandManager.literal("spmh")
                    .then(ClientCommandManager.literal("auth")
                            .executes(context -> dispatcher.execute("spmhelper auth", context.getSource()))
                    )
                    .then(ClientCommandManager.literal("status")
                            .executes(context -> dispatcher.execute("spmhelper status", context.getSource()))
                    );

            var payCommand = ClientCommandManager.literal("pay")
                    .then(ClientCommandManager.argument(Text.translatable("text.spmhelper.argumentForPayment.NickOrNumCard").getString(), StringArgumentType.string())
                            .then(ClientCommandManager.argument(Text.translatable("text.spmhelper.argumentForPayment.Amount").getString(), IntegerArgumentType.integer())
                                    .executes(context -> {
                                        String nickname = StringArgumentType.getString(context, Text.translatable("text.spmhelper.argumentForPayment.NickOrNumCard").getString());
                                        int amount = IntegerArgumentType.getInteger(context, Text.translatable("text.spmhelper.argumentForPayment.Amount").getString());
                                        MinecraftClient.getInstance().send(() -> {
                                            MinecraftClient.getInstance().setScreen(new PayScreen(nickname, amount));
                                        });
                                        return 1;
                                    })
                            ).executes(context -> {
                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.notEnteredAmount"));
                                return 1;
                            })
                    ).executes(context -> {
                        context.getSource().sendFeedback(Text.translatable("text.spmhelper.notEnteredNickOrCardNum"));
                        return 1;
                    });

            var deteciveCallCommand = createCallCommand("detective", Service.DETECTIVE);
            var fsbCallCommand = createCallCommand("fsb", Service.FSB);
            var bankerCallCommand = createCallCommand("banker", Service.BANKER);
            var guideCallCommand = createCallCommand("guide", Service.GUIDE);

            dispatcher.register(mainCommand);
            dispatcher.register(aliasMainCommand);

            dispatcher.register(payCommand);

            dispatcher.register(deteciveCallCommand);
            dispatcher.register(fsbCallCommand);
            dispatcher.register(bankerCallCommand);
            dispatcher.register(guideCallCommand);
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> createCallCommand(String commandName, Service service) {
        var callCommand = ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument(Text.translatable("text.spmhelper.argumentForCalls.Comms").getString(), StringArgumentType.greedyString())
                        .executes(context -> {
                            if (!hasToken) {
                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.status_FeedBackMessageCase401"));
                                SPmHelperApi.startAuthProcess(context.getSource().getPlayer());
                                return 0;
                            }
                            String comment = StringArgumentType.getString(context, Text.translatable("text.spmhelper.argumentForCalls.Comms").getString());
                            MinecraftClient.getInstance().send(() -> {
                                MinecraftClient.getInstance().setScreen(new ServiceAcceptScreen(service, comment, context.getSource().getPlayer()));
                            });
                            return 0;
                        })
                )
                .executes(context -> {
                    context.getSource().sendFeedback(Text.translatable("text.spmhelper.enterComment"));
                    return 0;
                });
        return callCommand;
    }
}
