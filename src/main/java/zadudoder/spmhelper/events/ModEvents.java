package zadudoder.spmhelper.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import zadudoder.spmhelper.SPmHelper;
import zadudoder.spmhelper.Screen.Pays.AddCardScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SPmHelperApi;

import static zadudoder.spmhelper.Screen.Calls.CallsScreen.ALLOWED_SERVERS;

@Environment(EnvType.CLIENT)
public class ModEvents {
    public static void registerEvents() {
        registerBlockClickHandler();
        registerChatEventHandler();
        registerCommands();
    }

    private static boolean hasToken = SPmHelperConfig.get().getAPI_TOKEN() != null && !SPmHelperConfig.get().getAPI_TOKEN().isEmpty();

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
                            if (Misc.isNumeric(amount)) {
                                String finalAmount = amount;
                                MinecraftClient.getInstance().
                                        execute(() -> MinecraftClient.getInstance().setScreen(new PayScreen(cardNumber, finalAmount, comment)));
                                return ActionResult.SUCCESS;
                            }
                        }
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
            ServerInfo serverInfo = client.getCurrentServerEntry();
            if (serverInfo != null) {
                String serverAddress = serverInfo.address;
                String domain = serverAddress.split(":")[0];

                boolean onAllowedServer = ALLOWED_SERVERS.stream().anyMatch(allowed ->
                        domain.equals(allowed) ||
                                domain.startsWith(allowed + ":"));

                if (onAllowedServer) {
                    String clientVersion = FabricLoader.getInstance().getModContainer(SPmHelper.MOD_ID).get().getMetadata().getVersion().toString();
                    String lastVersion = SPmHelperApi.getLastModVersionInfo().get("version_number").getAsString();

                    if (!clientVersion.equals(lastVersion)) {
                        client.player.sendMessage(
                                Text.literal("[SPmHelper]: Доступно обновление! ")
                                        .formatted(Formatting.GREEN)
                                        .styled(style -> style.withClickEvent(
                                                new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/spmhelper/version/" + lastVersion)
                                        ))
                                        .append(Text.literal(clientVersion).formatted(Formatting.YELLOW))
                                        .append(" -> ")
                                        .append(Text.literal(lastVersion).formatted(Formatting.GREEN))
                        );
                    }
                }
            }
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
                                context.getSource().sendFeedback(Text.literal("§a[SPmHelper]: Токен активен! Новая авторизация не нужна."));
                                return 0;
                            })
                    )
                    .then(ClientCommandManager.literal("status")
                            .executes(context -> {
                                SPmHelperApi.getAuthStatus().thenAccept(status -> {
                                    String message = switch (status) {
                                        case 200 -> "§a[SPmHelper]: Токен работает";
                                        case 401 -> "§c[SPmHelper]: Токен недействителен";
                                        default -> "§c[SPmHelper]: Ошибка API: " + status;
                                    };
                                    context.getSource().sendFeedback(Text.literal(message));
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

            dispatcher.register(mainCommand);
            dispatcher.register(aliasMainCommand);

        });
    }
}
