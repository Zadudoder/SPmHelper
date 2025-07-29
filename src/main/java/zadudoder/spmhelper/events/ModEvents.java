package zadudoder.spmhelper.events;

import com.google.common.base.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.ClickEvent;
import zadudoder.spmhelper.tutorial.ParticleManager;

@Environment(EnvType.CLIENT)
public class ModEvents {
    public static void registerEvents() {
        BlockClickHandler.registerBlockClickHandler();
        ChatEventHandler.registerChatEventHandler();
        Commands.registerCommands();
        ParticleManager.registerParticleManager();
    }
}
