package zadudoder.spmhelper.tutorial;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

public class TutorialPoint {
    public BlockPos pos;
    public RegistryKey<?> world;
    public Runnable action;
    public boolean last;

    public TutorialPoint(int x, int y, int z, RegistryKey<?> world, Runnable action, Boolean last) {
        this.pos = new BlockPos(x, y, z);
        this.world = world;
        this.action = action;
        this.last = last;
    }
}
