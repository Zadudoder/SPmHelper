package zadudoder.spmhelper;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.Text;
import zadudoder.spmhelper.config.SPmHelperConfig;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
                return AutoConfig.getConfigScreen(SPmHelperConfig.class, parent).get();
            }
            return new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(parent), Text.of("SPmHelper"), Text.of("SPmHelper requires Cloth Config to configure the mod"));
        };
    }
}
