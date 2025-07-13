package zadudoder.spmhelper.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zadudoder.spmhelper.utils.types.BranchCoords;
import zadudoder.spmhelper.utils.types.HubBranch;

import java.util.Arrays;
import java.util.List;


public class Misc {
    public static List<String> ALLOWED_SERVERS = Arrays.asList(
            "spm.spworlds.org",
            "spm.spworlds.ru"
    );

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

    public static BranchCoords getBranch(BlockPos blockPos) {
        int x = blockPos.getX();
        int z = blockPos.getZ();
        int absX = Math.abs(x);
        int absZ = Math.abs(z);

        final int BRANCH_BORDER = 40;
        if (absX <= BRANCH_BORDER && absZ <= BRANCH_BORDER) {
            return new BranchCoords(HubBranch.HUB, 0);
        }
        boolean onDiagonal = (x == z) || (x == -z);

        if (onDiagonal && absX > BRANCH_BORDER) {
            if (x < -BRANCH_BORDER && z < -BRANCH_BORDER) {
                return new BranchCoords(HubBranch.PURPLE, absX); // Красная+Синяя
            } else if (x > BRANCH_BORDER && z < -BRANCH_BORDER) {
                return new BranchCoords(HubBranch.ORANGE, absX); // Красная+Зелёная
            } else if (x > BRANCH_BORDER && z > BRANCH_BORDER) {
                return new BranchCoords(HubBranch.LIME, absX); // Жёлтая+Зелёная
            } else if (x < -BRANCH_BORDER && z > BRANCH_BORDER) {
                return new BranchCoords(HubBranch.CYAN, absX); // Жёлтая+Синяя
            }
        }
        if (z < -BRANCH_BORDER && absZ >= absX) {
            return new BranchCoords(HubBranch.RED, absZ);
        } else if (x < -BRANCH_BORDER && absX >= absZ) {
            return new BranchCoords(HubBranch.BLUE, absX);
        } else if (x > BRANCH_BORDER && absX >= absZ) {
            return new BranchCoords(HubBranch.GREEN, absX);
        } else if (z > BRANCH_BORDER && absZ >= absX) {
            return new BranchCoords(HubBranch.YELLOW, absZ);
        }

        return new BranchCoords(HubBranch.HUB, 0);
    }

    public static boolean isOnAllowedServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo serverInfo = client.getCurrentServerEntry();
        if (serverInfo == null) {
            return false;
        }

        String serverAddress = serverInfo.address;
        String domain = serverAddress.split(":")[0];

        boolean onAllowedServer = ALLOWED_SERVERS.stream().anyMatch(allowed ->
                domain.equals(allowed) ||
                        domain.startsWith(allowed + ":"));

        return onAllowedServer;

    }

    public static String getWorldName(World world) {
        String name = switch (world.getRegistryKey().getValue().toString()) {
            case "minecraft:the_nether" -> "Ад";
            case "minecraft:the_end" -> "Энд";
            default -> "Верхний мир";
        };
        return name;
    }

}

/*
Красная - Z-
Синяя X-
Зелёная X+
Жёлтая Z+
 */