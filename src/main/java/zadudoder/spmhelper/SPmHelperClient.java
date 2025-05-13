package zadudoder.spmhelper;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Laws.LawsScreen;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.Screen.Map.MapScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.events.ModEvents;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.types.Card;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class SPmHelperClient implements ClientModInitializer {
    public static SPmHelperConfig config = null;

    private static KeyBinding keyOpenScreen;
    private static KeyBinding keyOpenCallsScreen;
    private static KeyBinding keyOpenPayScreen;
    private static KeyBinding keyOpenMapScreen;
    private static KeyBinding keyOpenLawsScreen;

    @Override
    public void onInitializeClient() {

        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(SPmHelperConfig.class, JanksonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(SPmHelperConfig.class).getConfig();
        }

        registerKeyBindings();
        registerKeyHandlers();

        ModEvents.registerEvents();

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
                                String token = SPmHelperClient.config.getAPI_TOKEN();
                                context.getSource().sendError(Text.literal(
                                        "❌ Сначала привяжите карту: /spmhelper <id> <token>"));

                                MinecraftClient.getInstance().setScreen(new PayScreen());
                                return 1;
                            })
            );
        });
    }

    private void registerKeyBindings() {
        keyOpenScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_main_screen",
                GLFW.GLFW_KEY_H,
                "category.spmhelper"
        ));

        keyOpenCallsScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_calls_screen",
                GLFW.GLFW_KEY_C,
                "category.spmhelper"
        ));

        keyOpenPayScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_pay_screen",
                GLFW.GLFW_KEY_P,
                "category.spmhelper"
        ));

        keyOpenMapScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_map_screen",
                GLFW.GLFW_KEY_M,
                "category.spmhelper"
        ));

        keyOpenLawsScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_laws_screen",
                GLFW.GLFW_KEY_L,
                "category.spmhelper"
        ));
    }

    private void registerKeyHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null) {
                if (keyOpenScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new MainScreen());
                } else if (keyOpenCallsScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new CallsScreen());
                } else if (keyOpenPayScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new PayScreen());
                } else if (keyOpenMapScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new MapScreen());
                } else if (keyOpenLawsScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new LawsScreen());
                }
            }
        });
    }

}