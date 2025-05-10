package zadudoder.spmhelper.Screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Map.MapScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;

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


        ButtonWidget TG = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://t.me/zadudoderTG");
        }).dimensions(width-20, 10, 15, 15).build();
        this.addDrawableChild(TG);


        String[] buttonLabels = {"Оплата", "Карта", "Вызовы", "Законы", "Смерти"};

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
            case 2: this.client.setScreen(new CallsScreen()); break;
            case 3:
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

        // Надписи над кнопками
        String[] labels = {"Оплата", "Карта", "Вызовы", "Законы", "Смерти"};
        for (int i = 0; i < buttonCount; i++) {
            int textX = startX + i * (buttonWidth + spacing) + buttonWidth/2;
            int textY = startY - 15;
            context.drawText(this.textRenderer, labels[i], textX - this.textRenderer.getWidth(labels[i])/2, textY, 0xFFFFFFFF, false);
        }
    }
}