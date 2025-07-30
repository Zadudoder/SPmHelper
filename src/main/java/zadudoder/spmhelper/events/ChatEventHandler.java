package zadudoder.spmhelper.events;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zadudoder.spmhelper.SPmHelper;
import zadudoder.spmhelper.Screen.Pays.AddCardScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.tutorial.TutorialManager;
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SPmHelperApi;

public class ChatEventHandler {

    public static void registerChatEventHandler() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().contains("Управление картой [Копир. токен] [Копир. айди]") &&
                    message.getSiblings().get(0).getSiblings().get(0).getStyle().getClickEvent().getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {

                String token = message.getSiblings().get(0).getSiblings().get(0).getStyle().getClickEvent().getValue();
                String id = message.getSiblings().get(0).getSiblings().get(1).getStyle().getClickEvent().getValue();
                String name = message.getString().substring(1);
                name = name.substring(0, name.indexOf(']'));
                String finalName = name;

                MinecraftClient.getInstance().player.sendMessage(
                        Text.translatable("text.spmhelper.AddCardDontOpenMessage"),
                        false
                );

                MinecraftClient.getInstance().
                        execute(() -> MinecraftClient.getInstance().setScreen(new AddCardScreen(id, token, finalName)));

                Text addCard = Text.translatable("text.spmhelper.addCardOpenScreen", finalName)
                        .styled(style -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        String.format("/spmhelper addcard %s %s %s", id, token, finalName)))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Text.translatable("text.spmhelper.addCardOpenScreen.hover_tip", finalName)))
                        );

                MinecraftClient.getInstance().player.sendMessage(
                        Text.translatable("", addCard),
                        false
                );
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

                    if (SPmHelperConfig.get().isFirstRun) {
                        client.player.sendMessage(Text.translatable("text.spmhelper.welcomeMessage"));
                        SPmHelperConfig.get().isFirstRun = false;
                        AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
                    }
                }

            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            TutorialManager.stopTutorial();
        });
    }

}
