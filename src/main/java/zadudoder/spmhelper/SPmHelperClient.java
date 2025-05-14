package zadudoder.spmhelper;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Laws.LawsScreen;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.Screen.Map.MapScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.events.ModEvents;

@Environment(EnvType.CLIENT)
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