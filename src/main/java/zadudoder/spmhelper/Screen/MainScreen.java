package zadudoder.spmhelper.Screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Laws.LawsScreen;
import zadudoder.spmhelper.Screen.Map.MapScreen;
import zadudoder.spmhelper.Screen.Pays.MyCardScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;

import java.util.HashMap;
import java.util.Map;

public class MainScreen extends Screen {
    String result;
    int resultvalue;
    private boolean mouseOverText;
    private TextFieldWidget textField;
    Map<String, Integer> wordValues = new HashMap<>();

    // Координаты и размеры кнопок
    private int buttonWidth = 80;
    private int buttonHeight = 20;
    private int startX;
    private int startY;
    private int spacing = 10;
    private int buttonCount = 5;

    public MainScreen() {
        super(Text.of("Основной экран"));
    }

    @Override
    protected void init() {

        int totalWidth = (buttonWidth * buttonCount) + (spacing * (buttonCount - 1));
        startX = (width - totalWidth) / 2;
        startY = height / 2 - 30;


        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spworlds.ru/spm/groups/06c25d05-b370-47d4-8416-fa1011ea69a1");
        }).dimensions(width-20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);


        String[] buttonLabels = {"Оплата", "Карта", "Настройки", "Вызовы", "Законы"};

        for (int i = 0; i < buttonCount; i++) {
            final int buttonIndex = i;
            ButtonWidget button = ButtonWidget.builder(Text.of(buttonLabels[i]), (btn) -> {
                handleButtonClick(buttonIndex);
            }).dimensions(startX + i * (buttonWidth + spacing), startY, buttonWidth, buttonHeight).build();
            this.addDrawableChild(button);
        }

    }

    private void handleButtonClick(int buttonLabel) {
        switch(buttonLabel) {
            case 0: this.client.setScreen(new PayScreen()); break;
            case 1: this.client.setScreen(new MapScreen()); break;
            case 2: this.client.setScreen(new MyCardScreen()); break; //Тут должен быть экран который открывает настройки или MyCardScreen или что-то ещё
            case 3: this.client.setScreen(new CallsScreen()); break;
            case 4: this.client.setScreen(new LawsScreen()); break;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Identifier MainText = Identifier.of("spmhelper", "titles/spmhelpertextmain.png");
        int imageY = startY - 100 - 15; // 15 - отступ от кнопок
        int originalWidth = 932/2;
        int originalHeight = 152/2;
        int availableWidth = width - 40; // С отступами по 20px с каждой стороны
        int finalWidth = originalWidth;
        int finalHeight = originalHeight;
        if (originalWidth > availableWidth) {
            float scale = (float)availableWidth / originalWidth;
            finalWidth = availableWidth;
            finalHeight = (int)(originalHeight * scale);
        }
        int imageX = (width - finalWidth) / 2;
        context.drawTexture(MainText, imageX, imageY, 0, 0, finalWidth, finalHeight, finalWidth, finalHeight);
    }
}