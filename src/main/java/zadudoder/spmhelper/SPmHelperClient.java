package zadudoder.spmhelper;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.types.Card;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class SPmHelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Команда для привязки карты
            dispatcher.register(
                    literal("spmhelper")
                            .then(argument("id", StringArgumentType.string())
                                    .then(argument("token", StringArgumentType.string())
                                            .executes(context -> {
                                                String id = StringArgumentType.getString(context, "id");
                                                String token = StringArgumentType.getString(context, "token");

                                                SPmHelperConfig.setToken(id, token);
                                                Card card = new Card("PlayerCard", id, token);

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
            dispatcher.register(
                    literal("sptransfer")
                            .executes(context -> {
                                String id = SPmHelperConfig.getId();
                                String token = SPmHelperConfig.getToken();

                                if (id == null || token == null) {
                                    context.getSource().sendError(Text.literal(
                                            "❌ Сначала привяжите карту: /spmhelper <id> <token>"));
                                    return 1;
                                }

                                MinecraftClient.getInstance().setScreen(new PayScreen());
                                return 1;
                            })
            );
        });
    }
}