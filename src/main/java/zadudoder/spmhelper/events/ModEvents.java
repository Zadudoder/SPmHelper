package zadudoder.spmhelper.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModEvents {
    public static void registerEvents() {
        BlockClickHandler.registerBlockClickHandler();
        ChatEventHandler.registerChatEventHandler();
        Commands.registerCommands();
        ParticleManager.registerParticleManager();
    }
}
