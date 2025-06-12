package zadudoder.spmhelper;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.Text;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.ScreenType;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                return new NoticeScreen(
                        () -> MinecraftClient.getInstance().setScreen(parent),
                        Text.literal("SPmHelper"),
                        Text.literal("SPmHelper requires Cloth Config to configure the mod!")
                );
            }

            SPmHelperConfig config = SPmHelperConfig.get();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("text.spmhelper.title"))
                    .setSavingRunnable(() -> AutoConfig.getConfigHolder(SPmHelperConfig.class).save());

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("text.spmhelper.config.category"));

            // Добавляем настройки
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.spmhelper.option.enableMenuButton"), config.enableMenuButton)
                    .setSaveConsumer(newValue -> config.enableMenuButton = newValue)
                    .build());

            general.addEntry(entryBuilder.startEnumSelector(Text.translatable("text.spmhelper.option.defaultScreen"), ScreenType.class, config.defaultScreen)
                    .setSaveConsumer(newValue -> config.defaultScreen = newValue)
                    .setEnumNameProvider(value -> {
                        // Локализация значений enum
                        return Text.translatable("text.spmhelper.screen_type." + value.name().toLowerCase());
                    })
                    .build());

            return builder.build();
        };
    }
}
