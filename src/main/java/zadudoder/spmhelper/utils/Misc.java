package zadudoder.spmhelper.utils;

import net.minecraft.util.math.BlockPos;
import zadudoder.spmhelper.utils.types.BranchCoords;
import zadudoder.spmhelper.utils.types.HubBranch;

public class Misc {
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static BranchCoords getBranch(BlockPos blockPos){
        BranchCoords branchCoords = new BranchCoords();
        int absX = Math.abs(blockPos.getX());
        int absZ = Math.abs(blockPos.getZ());
        if(blockPos.getZ()<-50 && absZ>absX){
            branchCoords.branch = HubBranch.RED;
            branchCoords.pos = absZ;
        } else
        if(blockPos.getX()<-50 && absX>absZ){
            branchCoords.branch = HubBranch.BLUE;
            branchCoords.pos = absX;
        } else
        if(blockPos.getX()>50 && absX>absZ){
            branchCoords.branch = HubBranch.GREEN;
            branchCoords.pos = absX;
        } else
        if(blockPos.getZ()>50 && absZ>absX){
            branchCoords.branch = HubBranch.YELLOW;
            branchCoords.pos = absZ;
        }
        return branchCoords;
    }
}

/*
Красная - Z-
Синяя X-
Зелёная X+
Жёлтая Z+
 */