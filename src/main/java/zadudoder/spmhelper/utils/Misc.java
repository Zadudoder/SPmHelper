package zadudoder.spmhelper.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import zadudoder.spmhelper.utils.types.BranchCoords;
import zadudoder.spmhelper.utils.types.HubBranch;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.*;


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

    public static void ScanQrCode(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        BufferedImage screenshot;
        try {
            System.out.println("1. Захват экрана");
            screenshot = captureScreen();

            if (screenshot == null) {
                client.player.sendMessage(Text.of("§cНе удалось сделать скриншот"), false);
                return;
            }
        } catch (Exception ex) {
            return;
        }
        String result = decodeQRCode(screenshot);
        if (result == null) {
            client.player.sendMessage(Text.of("§cНе удалось отсканировать QR-CODE"), false);
            return;
        }
        client.player.sendMessage(
                Text.translatable("§a[SPmHelper] Ссылка прочитана, нажмите на сообщение для открытия ссылки")
                        .styled(style -> style.withClickEvent(
                                new ClickEvent(ClickEvent.Action.OPEN_URL, result)
                        ))
        );
    }

    private static BufferedImage captureScreen() {
        try {
            GLFW.glfwPollEvents();
            int width = MinecraftClient.getInstance().getWindow().getWidth();
            int height = MinecraftClient.getInstance().getWindow().getHeight();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

            ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
            GLFW.glfwGetFramebufferSize(MinecraftClient.getInstance().getWindow().getHandle(),
                    new int[1], new int[1]);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            buffer.asIntBuffer().get(pixels);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String decodeQRCode(BufferedImage image) {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(DecodeHintType.ALSO_INVERTED, Boolean.TRUE);

        LuminanceSource source = new BufferedImageLuminanceSource(image);

        String result = tryDecodeWithStrategies(source, hints);

        if (result == null) {
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            result = tryDecodeWithStrategies(source, hints);
        }

        return result;
    }

    private static String tryDecodeWithStrategies(LuminanceSource source, Map<DecodeHintType, Object> hints) {
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            return new MultiFormatReader().decode(bitmap, hints).getText();
        } catch (NotFoundException e) {
        }

        try {
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            return new MultiFormatReader().decode(bitmap, hints).getText();
        } catch (NotFoundException e) {
        }
        try {
            GenericMultipleBarcodeReader reader = new GenericMultipleBarcodeReader(new MultiFormatReader());
            Result[] results = reader.decodeMultiple(new BinaryBitmap(new HybridBinarizer(source)), hints);
            if (results.length > 0) {
                return results[0].getText();
            }
        } catch (NotFoundException e) {
        }

        return null;
    }
}

/*
Красная - Z-
Синяя X-
Зелёная X+
Жёлтая Z+
 */