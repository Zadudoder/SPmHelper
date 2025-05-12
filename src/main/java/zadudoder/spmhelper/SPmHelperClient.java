package zadudoder.spmhelper;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.types.Card;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class SPmHelperClient implements ClientModInitializer {
    public static SPmHelperConfig config = null;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(SPmHelperConfig.class, JanksonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(SPmHelperConfig.class).getConfig();
        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Команда для привязки карты
            dispatcher.register(
                    literal("spmhelper")
                            .then(argument("id", StringArgumentType.string())
                                    .then(argument("token", StringArgumentType.string())
                                            .executes(context -> {
                                                String id = StringArgumentType.getString(context, "id");
                                                String token = StringArgumentType.getString(context, "token");

                                                //SPmHelperConfig.setToken(id, token);
                                                SPmHelperClient.config.setSpID(id);
                                                SPmHelperClient.config.setSpTOKEN(token);
                                                AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
                                                Card card = new Card(id, token);

                                                int balance = SPWorldsApi.getBalance(card);
                                                if (balance >= 0) {
                                                    context.getSource().sendFeedback(Text.literal(
                                                            "✅ Карта привязана! Баланс: " + balance + " AR"));
                                                } else {
                                                    context.getSource().sendError(Text.literal(
                                                            "❌ Ошибка: неверный токен или ID"));
                                                }
                                                return 1;
                                            }))
                            ));

            // Команда для открытия экрана перевода
            //Я сломал @R4mBLe_
            dispatcher.register(
                    literal("sptransfer")
                            .executes(context -> {
                                String token = SPmHelperClient.config.getTOKEN();
                                context.getSource().sendError(Text.literal(
                                        "❌ Сначала привяжите карту: /spmhelper <id> <token>"));

                                MinecraftClient.getInstance().setScreen(new PayScreen());
                                return 1;
                            })
            );
        });
    }
}