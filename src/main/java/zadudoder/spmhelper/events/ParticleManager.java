package zadudoder.spmhelper.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zadudoder.spmhelper.TutorialManager;

public class ParticleManager {
    public static void registerParticleManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!TutorialManager.isEnabled) {
                return;
            }
            PlayerEntity player = client.player;
            World world = player.getWorld();
            BlockPos deletePos = null;
            for (int index = 0; index < TutorialManager.checkpoints.size(); index++) {
                BlockPos checkpointPosition = TutorialManager.checkpoints.get(index).pos;
                world.addParticle(
                        ParticleTypes.FLAME,
                        checkpointPosition.getX(), checkpointPosition.getY(), checkpointPosition.getZ(),
                        5, 0.2, 0.2
                );
                if (player.getBlockPos() == checkpointPosition) {
                    deletePos = checkpointPosition;
                }
            }
            if (deletePos == null) {
                return;
            }
            while (TutorialManager.checkpoints.getFirst().pos != deletePos) {
                Runnable action = TutorialManager.checkpoints.getFirst().action;
                TutorialManager.checkpoints.remove(0);
                if (action != null) {
                    action.run();
                }
            }
        });
    }
}
