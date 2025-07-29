package zadudoder.spmhelper.tutorial;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class TutorialManager {
    public static boolean isEnabled;

    public static ArrayList<TutorialPoint> checkpoints;

    public static void startTutorial() {
        isEnabled = true;
        checkpoints = new ArrayList<>();
        StartTutorial();
        pullText("Включите частицы в настройках и направляйтесь в путь по ним.");
    }

    public static void stopTutorial() {
        isEnabled = false;
    }

    private static void pullText(String text) {
        MinecraftClient.getInstance().player.sendMessage(Text.literal(text));
    }

    private static void StartTutorial(){
        checkpoints.add(new TutorialPoint(18, 81, 16,null, false));
        checkpoints.add(new TutorialPoint(14, 81, 22,null, false));
        checkpoints.add(new TutorialPoint(11, 81, 28,null, false));
        checkpoints.add(new TutorialPoint((int) 7.5, 81, 33, TutorialManager::PNAction, true));
    }

    private static void PNAction() {
        pullText("Вы пришли в палатку новичков");
        startBankTutorial();
    }

    private static void startBankTutorial() {
        checkpoints.add(new TutorialPoint(10, 81, 30, null, false));
        checkpoints.add(new TutorialPoint(6, 81, 23, null, false));
        checkpoints.add(new TutorialPoint(0, 81, 19, null, false));
        checkpoints.add(new TutorialPoint(-7, 81, 16, null, false));
        checkpoints.add(new TutorialPoint(-16, 79, 16, null, false));
        checkpoints.add(new TutorialPoint(-24, 79, 12, null, false));
        checkpoints.add(new TutorialPoint(-32, 78, 8, null, false));
        checkpoints.add(new TutorialPoint(-36, 79, 14, TutorialManager::BankAction, true));
    }

    private static void BankAction() {
        pullText("Вы пришли в банк");
        startCIKTutorial();
    }

    private static void startCIKTutorial() {
        checkpoints.add(new TutorialPoint(-33, 79, 9, null, false));
        checkpoints.add(new TutorialPoint(-32, 78, 2, null, false));
        checkpoints.add(new TutorialPoint(-31, 78, -8, TutorialManager::CIKAction, true));
    }

    private static void CIKAction() {
        pullText("Вы пришли в Центр Избирательной Комиссии (ЦИК)");
        startGalleryTutorial();
    }

    private static void startGalleryTutorial() {
        checkpoints.add(new TutorialPoint(-31, 78, -17,null, false));
        checkpoints.add(new TutorialPoint(-30, 78, -26,null, false));
        checkpoints.add(new TutorialPoint(-29, 75, -34,null, false));
        checkpoints.add(new TutorialPoint(-24, 73, -41,null, false));
        checkpoints.add(new TutorialPoint(-17, 72, -48,null, false));
        checkpoints.add(new TutorialPoint(-10, 72, -56,null, false));
        checkpoints.add(new TutorialPoint(-6, 71, -63,null, false));
        checkpoints.add(new TutorialPoint(-3, 71, -75,null, false));
        checkpoints.add(new TutorialPoint(2, 70, -81,null, false));
        checkpoints.add(new TutorialPoint(11, 70, -88,null, false));
        checkpoints.add(new TutorialPoint(20, 68, -93,null, false));
        checkpoints.add(new TutorialPoint(29, 64, -97,null, false));
        checkpoints.add(new TutorialPoint(37, 66, -104,null, false));
        checkpoints.add(new TutorialPoint(44, 65, -111,null, false));
        checkpoints.add(new TutorialPoint(54, 63, -119,null, false));
        checkpoints.add(new TutorialPoint(59, 63, -126,null, false));
        checkpoints.add(new TutorialPoint(59, 64, -139,null, false));
        checkpoints.add(new TutorialPoint(59, 64, -147,null, false));
        checkpoints.add(new TutorialPoint(59, 50, -154,null, false));
        checkpoints.add(new TutorialPoint(59, 50, -166, TutorialManager::GalleryAction, true));
    }

    private static void GalleryAction() {
        pullText("Вы пришли в галерею.");
        startLawCourtTutorial();
    }

    private static void startLawCourtTutorial() {
        checkpoints.add(new TutorialPoint(-31, 78, -17, null, false));
    }

}
