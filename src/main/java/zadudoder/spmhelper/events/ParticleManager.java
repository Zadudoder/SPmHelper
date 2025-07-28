package zadudoder.spmhelper.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zadudoder.spmhelper.TutorialManager;
import zadudoder.spmhelper.utils.Misc;

public class ParticleManager {
    public static void registerParticleManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!TutorialManager.isEnabled) {
                return;
            }
            PlayerEntity player = client.player;
            if (player == null) {
                return;
            }
            World world = player.getWorld();

            BlockPos deletePos = null;
            for (int index = 0; index < TutorialManager.checkpoints.size(); index++) {
                BlockPos checkpointPosition = TutorialManager.checkpoints.get(index).pos;
                world.addParticle(
                        ParticleTypes.FLAME,
                        checkpointPosition.getX(), checkpointPosition.getY(), checkpointPosition.getZ(),
                        0, 0.1, 0
                );
                if (Misc.getDistance(player.getBlockPos(), checkpointPosition) <= 1) {
                    deletePos = checkpointPosition;
                }
            }
            if (deletePos == null) {
                return;
            }
            while (!TutorialManager.checkpoints.getFirst().pos.toCenterPos().equals(deletePos.toCenterPos())) {
                TutorialManager.checkpoints.removeFirst();
            }
            Runnable action = TutorialManager.checkpoints.getFirst().action;
            TutorialManager.checkpoints.removeFirst();
            if (action != null) {
                action.run();
            }
            if (TutorialManager.checkpoints.isEmpty()) {
                TutorialManager.stopTutorial();
            }
        });
    }
}
