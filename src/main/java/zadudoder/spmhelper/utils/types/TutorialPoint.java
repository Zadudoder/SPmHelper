package zadudoder.spmhelper.utils.types;

import net.minecraft.util.math.BlockPos;

public class TutorialPoint {
    public BlockPos pos;
    public Runnable action;

    public TutorialPoint(int x, int y, int z, Runnable action) {
        this.pos = new BlockPos(x, y, z);
        this.action = action;
    }
}
