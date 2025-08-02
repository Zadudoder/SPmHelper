package zadudoder.spmhelper.tutorial;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;

import static net.minecraft.world.World.*;

public class TutorialManager {
    public static boolean isEnabled;

    public static ArrayList<TutorialPoint> checkpoints;

    public static void preTutorial() {
        isEnabled = true;
        checkpoints = new ArrayList<>();
    }

    public static void startFullTutorial() {
        pullText("Включите частицы в настройках и направляйтесь в путь по ним.");
        startPNTutorial();
    }

    public static void stopTutorial() {
        isEnabled = false;
        if (checkpoints != null) {
            checkpoints.clear();
            pullText("Туториал окончен");
        } else pullText("Вы не начинали туториал");
    }

    public static void skipTutorial() {
        Runnable action = checkpoints.getLast().action;
        checkpoints.clear();
        action.run();
    }

    private static void pullText(String text) {
        MinecraftClient.getInstance().player.sendMessage(Text.literal(text));
    }

    public static void startPNTutorial() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
        checkpoints.add(new TutorialPoint(18, 81, 16, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(14, 81, 22, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(11, 81, 28, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(7, 81, 33, OVERWORLD, TutorialManager::PNAction, true));
    }

    private static void PNAction() {
        pullText("Вы пришли в палатку новичков");
        startCIKTutorial();
    }

    public static void startCIKTutorial() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
        checkpoints.add(new TutorialPoint(10, 81, 30, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(6, 81, 23, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(0, 81, 19, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-7, 81, 16, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-16, 79, 16, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-24, 79, 12, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-27, 78, 6, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-28, 78, 0, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-31, 78, -8, OVERWORLD, TutorialManager::CIKAction, true));
    }

    private static void CIKAction() {
        pullText("Вы пришли в Центр Избирательной Комиссии (ЦИК)");
        startBankTutorial();
    }

    public static void startBankTutorial() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
        checkpoints.add(new TutorialPoint(-31, 78, -17, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-30, 78, -26, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-29, 75, -34, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-25, 73, -42, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-25, 72, -49, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-28, 71, -55, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-27, 71, -59, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-26, 72, -66, OVERWORLD, TutorialManager::BankAction, true));
    }

    private static void BankAction() {
        pullText("Вы пришли в банк");
        startGalleryTutorial();
    }

    public static void startGalleryTutorial() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
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
        checkpoints.add(new TutorialPoint(59, 50, -160, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 50, -166, OVERWORLD, TutorialManager::GalleryAction, true));
    }

    private static void GalleryAction() {
        pullText("Вы пришли в галерею");
        startLawCourtTutorial();
    }

    public static void startLawCourtTutorial() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
        checkpoints.add(new TutorialPoint(59, 50, -160, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(59, 50, -154, OVERWORLD, () -> pullText("Поднимайтесь на верх"), false)); // Написать поднимайтесь наверх
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

    public static void startGoToHell() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
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

    public static void startGoToEndAndTalkAboutTrade() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
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
        checkpoints.add(new TutorialPoint(-1266, 20, 1036, OVERWORLD, TutorialManager::GoToEndAction, true));
    }

    private static void GoToEndAction() {
        pullText("Прыгайте портал");
        StartEnd();
    }

    public static void StartEnd() {
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

    public static void goToFSB() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
        checkpoints.add(new TutorialPoint(11, 81, 25, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(12, 81, 32, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(14, 80, 44, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(16, 80, 52, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 80, 60, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 80, 66, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 76, 74, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(20, 72, 82, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(16, 71, 91, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(12, 71, 99, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(11, 71, 108, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(4, 71, 110, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(0, 71, 111, OVERWORLD, TutorialManager::FSBAction, true));
    }

    private static void FSBAction() {
        pullText("Вы у здания ФСБ, идём к библиотеке");
        goToBiblioteka();
    }

    public static void goToBiblioteka() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }
        checkpoints.add(new TutorialPoint(-1, 71, 110, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-7, 71, 109, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(11, 71, 106, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-14, 72, 108, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-21, 72, 104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-26, 72, 104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-34, 72, 104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-42, 72, 104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-48, 72, 104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-53, 72, 104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-61, 71, 105, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-68, 72, 106, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-75, 73, 107, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-84, 73, 108, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-92, 72, 110, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-99, 71, 111, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-104, 71, 114, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-104, 71, 121, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-106, 71, 125, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-110, 72, 127, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-111, 72, 132, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-112, 72, 138, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-116, 72, 144, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-121, 72, 148, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-126, 72, 149, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-131, 72, 146, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-136, 72, 143, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-136, 71, 135, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-139, 71, 131, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-136, 71, 125, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-136, 72, 119, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-133, 72, 116, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-127, 72, 114, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-120, 72, 115, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-116, 72, 120, OVERWORLD, TutorialManager::BibliotekaAction, true));
    }

    private static void BibliotekaAction() {
        pullText("Вы посетили библиотеку");
        goToSpawn();
    }

    public static void goToSpawn() {
        if (!isEnabled || checkpoints == null) {
            preTutorial();
        }

        checkpoints.add(new TutorialPoint(-105, 71, 124, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-104, 71, 118, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-102, 71, 112, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-96, 72, 110, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-88, 73, 109, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-78, 73, 107, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-69, 73, 106, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-62, 71, 104, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-60, 71, 99, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-61, 72, 93, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-62, 73, 84, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-61, 73, 75, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-61, 73, 67, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-61, 73, 59, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-63, 75, 53, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-64, 76, 47, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-60, 75, 43, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-54, 72, 36, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-48, 72, 31, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-44, 74, 29, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-39, 77, 28, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-31, 79, 24, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-24, 79, 20, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-18, 79, 17, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-11, 80, 16, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(-2, 81, 17, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(4, 81, 22, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(10, 81, 27, OVERWORLD, null, false));
        checkpoints.add(new TutorialPoint(7, 81, 33, OVERWORLD, TutorialManager::SpawnAction, true));
    }

    private static void SpawnAction() {
        pullText("Вы закончили экскурсию!");
    }
}