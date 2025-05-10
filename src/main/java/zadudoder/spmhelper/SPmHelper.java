package zadudoder.spmhelper;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Laws.LawsScreen;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.Screen.Map.MapScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;

public class SPmHelper implements ModInitializer {
	public static final String MOD_ID = "spmhelper";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static KeyBinding keyOpenScreen;
	private static KeyBinding keyOpenCallsScreen;
	private static KeyBinding keyOpenPayScreen;
	private static KeyBinding keyOpenMapScreen;
	private static KeyBinding keyOpenLawsScreen;

	@Override
	public void onInitialize() {
		keyOpenScreen = new KeyBinding("key.spmhelper.open_main_screen", GLFW.GLFW_KEY_H, "category.spmhelper");
		keyOpenCallsScreen = new KeyBinding("key.spmhelper.open_calls_screen", GLFW.GLFW_KEY_C, "category.spmhelper");
		keyOpenPayScreen = new KeyBinding("key.spmhelper.open_pay_screen", GLFW.GLFW_KEY_P, "category.spmhelper");
		keyOpenMapScreen = new KeyBinding("key.spmhelper.open_map_screen", GLFW.GLFW_KEY_M, "category.spmhelper");
		keyOpenLawsScreen = new KeyBinding("key.spmhelper.open_laws_screen", GLFW.GLFW_KEY_L, "category.spmhelper");
		KeyBindingHelper.registerKeyBinding(keyOpenScreen);
		KeyBindingHelper.registerKeyBinding(keyOpenCallsScreen);
		KeyBindingHelper.registerKeyBinding(keyOpenPayScreen);
		KeyBindingHelper.registerKeyBinding(keyOpenMapScreen);
		KeyBindingHelper.registerKeyBinding(keyOpenLawsScreen);

		// Устанавливаем слушатель нажатий клавиш
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.currentScreen == null && keyOpenScreen.wasPressed()) { openMainScreen(); }
			if (client.currentScreen == null && keyOpenCallsScreen.wasPressed()) { openCallsScreen(); }
			if (client.currentScreen == null && keyOpenPayScreen.wasPressed()) { openPayScreen(); }
			if (client.currentScreen == null && keyOpenMapScreen.wasPressed()) { openMapScreen(); }
			if (client.currentScreen == null && keyOpenLawsScreen.wasPressed()) { openLawsScreen(); }
		});

	}
	public void openMainScreen() {
		MinecraftClient.getInstance().setScreen(new MainScreen());
	}
	public void openCallsScreen() {
		MinecraftClient.getInstance().setScreen(new CallsScreen());
	}
	public void openPayScreen() { MinecraftClient.getInstance().setScreen(new PayScreen()); }
	public void openMapScreen() { MinecraftClient.getInstance().setScreen(new MapScreen()); }
	public void openLawsScreen() { MinecraftClient.getInstance().setScreen(new LawsScreen()); }
}