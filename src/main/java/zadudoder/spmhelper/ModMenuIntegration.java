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
            ConfigCategory mainCategory = builder.getOrCreateCategory(Text.translatable("text.spmhelper.config.mainCategory"));
            ConfigCategory navCategory = builder.getOrCreateCategory(Text.translatable("text.spmhelper.config.navCategory"));

            // Добавляем настройки
            mainCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.spmhelper.option.enableMenuButton"), config.enableMenuButton)
                    .setSaveConsumer(newValue -> config.enableMenuButton = newValue)
                    .build());

            mainCategory.addEntry(entryBuilder.startEnumSelector(Text.translatable("text.spmhelper.option.defaultScreen"), ScreenType.class, config.defaultScreen)
                    .setSaveConsumer(newValue -> config.defaultScreen = newValue)
                    .setEnumNameProvider(value -> {
                        // Локализация значений enum
                        return Text.translatable("text.spmhelper.screen_type." + value.name().toLowerCase());
                    })
                    .build());

            mainCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.spmhelper.option.numberOfCardInComment"), config.numberOfCardInComment)
                    .setSaveConsumer(newValue -> config.numberOfCardInComment = newValue)
                    .build());

            navCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("text.spmhelper.option.enableSPmHelperNav"), config.enableSPmNav)
                    .setSaveConsumer(newValue -> config.enableSPmNav = newValue)
                    .build());

            navCategory.addEntry(entryBuilder.startAlphaColorField(Text.translatable("text.spmhelper.option.SpmNavBackgroundColor"), config.SpmNavBackgroundColor)
                    .setSaveConsumer(newValue -> config.SpmNavBackgroundColor = newValue)
                    .build());

            navCategory.addEntry(entryBuilder.startIntSlider(Text.translatable("text.spmhelper.option.SPmHelperNavX"), config.SPmNavX, 1, 100)
                    .setTextGetter(value -> Text.literal(value + " %"))
                    .setSaveConsumer(newValue -> config.SPmNavX = newValue)
                    .build());

            navCategory.addEntry(entryBuilder.startIntSlider(Text.translatable("text.spmhelper.option.SPmHelperNavY"), config.SPmNavY, 1, 100)
                    .setTextGetter(value -> Text.literal(value + " %"))
                    .setSaveConsumer(newValue -> config.SPmNavY = newValue)
                    .build());

            navCategory.addEntry(entryBuilder.startIntSlider(Text.translatable("text.spmhelper.option.SPmHelperNavScale"), config.SPmNavScale, 1, 200)
                    .setTextGetter(value -> Text.literal(value + " %"))
                    .setSaveConsumer(newValue -> config.SPmNavScale = newValue)
                    .build());

            return builder.build();
        };
    }
}
