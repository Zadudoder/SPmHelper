package zadudoder.spmhelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import zadudoder.spmhelper.utils.types.TutorialPoint;

import java.util.ArrayList;

public class TutorialManager {
    public static boolean isEnabled;

    public static ArrayList<TutorialPoint> checkpoints;

    public static void startTutorial() {
        isEnabled = true;
        checkpoints = new ArrayList<>();
        startBankTutorial();
    }

    public static void stopTutorial() {
        isEnabled = false;
    }

    private static void startBankTutorial() {
        checkpoints.add(new TutorialPoint(11, 240, 25, null));
        checkpoints.add(new TutorialPoint(9, 240, 25, null));
        checkpoints.add(new TutorialPoint(7, 240, 33, TutorialManager::startBankAction));
    }

    private static void startBankAction() {
        MinecraftClient.getInstance().player.sendMessage(Text.literal("END TUTOR"));
    }
}
