package zadudoder.spmhelper;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SoundManager;

@Environment(EnvType.CLIENT)
public class SPmHelperClient implements ClientModInitializer {
    private static KeyBinding keyOpenScreen;
    private static KeyBinding keyOpenCallsScreen;
    private static KeyBinding keyOpenPayScreen;
    private static KeyBinding keyOpenMapScreen;
    private static KeyBinding keyOpenLawsScreen;
    private static KeyBinding keyScanQrCode;


    @Override
    public void onInitializeClient() {
        AutoConfig.register(SPmHelperConfig.class, GsonConfigSerializer::new);

        registerKeyBindings();
        registerKeyHandlers();
        SoundManager.initialize();
        ModEvents.registerEvents();

        //registerParticleRendering();
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

        keyScanQrCode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.scan_qr_code",
                GLFW.GLFW_KEY_R,
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
                } else if (keyScanQrCode.wasPressed()) {
                    Misc.ScanQrCode(MinecraftClient.getInstance());
                }
            }
        });
    }


}