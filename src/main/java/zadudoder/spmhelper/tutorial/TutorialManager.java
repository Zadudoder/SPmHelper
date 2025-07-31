package zadudoder.spmhelper.tutorial;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;

import static net.minecraft.world.World.*;

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
        checkpoints.clear();
    }

    public static void skipTutorial() {
        Runnable action = checkpoints.getLast().action;
        checkpoints.clear();
        action.run();
    }

    private static void pullText(String text) {
        MinecraftClient.getInstance().player.sendMessage(Text.literal(text));
    }

    private static void StartTutorial() {
        checkpoints.add(new TutorialPoint(18, 81, 16, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(14, 81, 22, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(11, 81, 28, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(7, 81, 33, OVERWORLD, TutorialManager::PNAction, true));
    }

    private static void PNAction() {
        pullText("Вы пришли в палатку новичков");
        startCIKTutorial();
    }

    private static void startCIKTutorial() {
        checkpoints.add(new TutorialPoint(10, 81, 30, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(6, 81, 23, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(0, 81, 19, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-7, 81, 16, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-16, 79, 16, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-24, 79, 12, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-27, 78, 6, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-32, 78, 2, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-31, 78, -8, OVERWORLD, TutorialManager::CIKAction, true));
    }

    private static void CIKAction() {
        pullText("Вы пришли в Центр Избирательной Комиссии (ЦИК)");
        startBankTutorial();
    }

    private static void startBankTutorial() {
        checkpoints.add(new TutorialPoint(-31, 78, -17, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-30, 78, -26, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-29, 75, -34, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-25, 73, -42, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-25, 72, -49, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-28, 71, -55, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-26, 72, -66, OVERWORLD, TutorialManager::BankAction, true));
    }

    private static void BankAction() {
        pullText("Вы пришли в банк");
        startGalleryTutorial();
    }

    private static void startGalleryTutorial() {
        checkpoints.add(new TutorialPoint(-22, 72, -67, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-17, 71, -68, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-10, 71, -72, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-3, 71, -75, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(2, 70, -81, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(11, 70, -88, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 68, -93, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(29, 64, -97, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(37, 66, -104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(44, 65, -111, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(54, 63, -119, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 63, -126, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 64, -139, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 64, -147, OVERWORLD, null, false)); // Отправить сообщение что нужно прыгать вниз
        checkpoints.add(new TutorialPoint(59, 50, -154, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 50, -166, OVERWORLD, TutorialManager::GalleryAction, true));
    }

    private static void GalleryAction() {
        pullText("Вы пришли в галерею");
        startLawCourtTutorial();
    }

    private static void startLawCourtTutorial() {
        checkpoints.add(new TutorialPoint(59, 50, -166, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 50, -154, OVERWORLD, null, false)); // Написать поднимайтесь наверх
        checkpoints.add(new TutorialPoint(59, 64, -147, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 64, -139, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 63, -126, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(54, 63, -119, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(44, 65, -111, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(37, 66, -104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(29, 64, -97, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 68, -93, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(11, 70, -88, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(3, 70, -82, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-4, 71, -75, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-6, 71, -63, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-1, 74, -53, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(5, 77, -46, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(11, 79, -37, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(16, 79, -27, OVERWORLD, TutorialManager::LawCourtAction, true));

    }
    private static void LawCourtAction() {
        pullText("Вы пришли в суд");
        startGoToHell();
    }

    private static void startGoToHell() {
        checkpoints.add(new TutorialPoint(16, 79, -24, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(10, 80, -18, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(8, 81, -12, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(8, 81, -3, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(8, 81, 3, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(10, 81, 9, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(10, 81, 12, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(0, 141, 7, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-2, 141, 8, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-8, 140, 9, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-15, 137, 16, NETHER, TutorialManager::HellAction, true));
    }

    private static void HellAction() {
        pullText("Вы в аду, едем к энду");
        startGoToEndAndTalkAboutTrade();
    }

    private static void startGoToEndAndTalkAboutTrade() {
        checkpoints.add(new TutorialPoint(-17, 137, 14, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-18, 136, 10, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-23, 131, 0, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-38, 130, 0, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-52, 130, 0, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-62, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-72, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-82, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-92, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-102, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-112, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-122, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-132, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-142, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-152, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-162, 130, 2, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-172, 130, 4, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-172, 130, 10, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 22, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 32, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 42, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 52, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 62, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 72, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 82, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 92, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 102, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 112, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-173, 130, 115, NETHER, null, false));
        checkpoints.add(new TutorialPoint(-1267, 20, 1038, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(96, 49, 0, END, null, false));
        checkpoints.add(new TutorialPoint(90, 49, 0, END, null, false));
        checkpoints.add(new TutorialPoint(87, 54, 0, END, null, false));
        checkpoints.add(new TutorialPoint(87, 60, 0, END, null, false));
        checkpoints.add(new TutorialPoint(82, 59, 0, END, null, false));
        checkpoints.add(new TutorialPoint(70, 59, 0, END, null, false));
        checkpoints.add(new TutorialPoint(58, 59, 0, END, null, false));
        checkpoints.add(new TutorialPoint(50, 59, 4, END, null, false));
        checkpoints.add(new TutorialPoint(42, 59, 9, END, null, false));
        checkpoints.add(new TutorialPoint(34, 59, 5, END, null, false));
        checkpoints.add(new TutorialPoint(28, 59, 0, END, null, false));
        checkpoints.add(new TutorialPoint(18, 59, 0, END, null, false));
        checkpoints.add(new TutorialPoint(8, 60, 0, END, null, false));
        checkpoints.add(new TutorialPoint(3, 61, 0, END, TutorialManager::EndAction, true)); // Соо прыгай в энд
    }

    private static void EndAction() {
        pullText("Прыгайте в портал и бегите к палатке новичков, там, где вы начинали.");
        goToFSB();
    }

    private static void goToFSB() {
        checkpoints.add(new TutorialPoint(11, 81, 25, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(12, 81, 32, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(14, 80, 44, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(16, 80, 52, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 80, 60, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 80, 66, OVERWORLD, null, false));
    }
}