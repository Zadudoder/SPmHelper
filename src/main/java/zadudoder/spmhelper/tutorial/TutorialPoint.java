package zadudoder.spmhelper.tutorial;

import net.minecraft.util.math.BlockPos;

public class TutorialPoint {
    public BlockPos pos;
    public Runnable action;
    public boolean last;

    public TutorialPoint(int x, int y, int z, Runnable action, Boolean last) {
        this.pos = new BlockPos(x, y, z);
        this.action = action;
        this.last = last;
    }
}
