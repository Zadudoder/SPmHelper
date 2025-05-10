package zadudoder.spmhelper;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.api.SPWorldsApi;
import zadudoder.spmhelper.api.types.Card;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;


public class SPmHelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("spmhelper")
                            .then(argument("token", StringArgumentType.string())
                                    .then(argument("id", StringArgumentType.string())
                                            .executes(context -> {
                                                String token = StringArgumentType.getString(context, "token");
                                                String id = StringArgumentType.getString(context, "id");

                                                SPmHelperConfig.setToken(id, token);

                                                int balance = SPWorldsApi.getBalance(new Card("PlayerCard", id, token));
                                                if (balance != -5298) {
                                                    context.getSource().sendFeedback(Text.literal("✅ Карта привязана! Баланс: " + balance + " AR"));
                                                } else {
                                                    context.getSource().sendError(Text.literal("❌ Ошибка: неверный токен или ID"));
                                                }
                                                return 1;
                                            })))
            );
        });

    }
}