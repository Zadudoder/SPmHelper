package zadudoder.spmhelper.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import zadudoder.spmhelper.Screen.QRcodeAcceptScreen;
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
        NativeImage screenshot;
        try {
            screenshot = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());
            if (screenshot == null) {
                client.player.sendMessage(Text.translatable("text.spmhelper.FailedTakeScreenshot"), false);
                return;
            }
        } catch (Exception ex) {
            return;
        }
        BufferedImage bufferedImage = new BufferedImage(
                screenshot.getWidth(),
                screenshot.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        for (int y = 0; y < screenshot.getHeight(); y++) {
            for (int x = 0; x < screenshot.getWidth(); x++) {
                int color = screenshot.getColor(x, y);
                bufferedImage.setRGB(x, y, color);
            }
        }
        String result = decodeQRCode(bufferedImage);
        if (result == null) {
            client.player.sendMessage(Text.translatable("text.spmhelper.QRCodeNotFound"), false);
            return;
        }

        Text clickableLink = Text.literal(result)
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, result))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.translatable("text.spmhelper.qrscanner.hover_tip")))
                );

        client.player.sendMessage(
                Text.translatable("text.spmhelper.foundLink", clickableLink),
                false
        );
        client.setScreen(new QRcodeAcceptScreen(result, client.currentScreen));
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