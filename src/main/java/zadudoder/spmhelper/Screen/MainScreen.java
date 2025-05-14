package zadudoder.spmhelper.Screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Laws.LawsScreen;
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

    public MainScreen() {
        super(Text.of("Основной экран"));
    }
    // Настройки кнопок
    private static final int MIN_BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;
    private static final int HORIZONTAL_SPACING = 10;
    private static final int VERTICAL_SPACING = 10;
    private static final int BUTTON_COUNT = 5;
    private static final int SCREEN_PADDING = 20;
    private static final int IMAGE_BOTTOM_MARGIN = 20; // Отступ между картинкой и кнопками

    @Override
    protected void init() {
        // Кнопка группы SPm (без изменений)
        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spworlds.ru/spm/groups/06c25d05-b370-47d4-8416-fa1011ea69a1");
        }).dimensions(width-20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);

        String[] buttonLabels = {"Оплата", "Карта", "Настройки", "Вызовы", "Законы"};

        // Рассчитываем доступную ширину для кнопок
        int availableWidth = width - 2 * SCREEN_PADDING;

        // Рассчитываем оптимальное количество кнопок в строке и их ширину
        int buttonsPerRow = Math.max(1, Math.min(
                BUTTON_COUNT,
                availableWidth / (MIN_BUTTON_WIDTH + HORIZONTAL_SPACING)
        ));

        int buttonWidth = Math.min(
                MIN_BUTTON_WIDTH,
                (availableWidth - (buttonsPerRow - 1) * HORIZONTAL_SPACING) / buttonsPerRow
        );

        // Рассчитываем общую высоту всех строк кнопок
        int totalRows = (int) Math.ceil((double) BUTTON_COUNT / buttonsPerRow);
        int totalButtonsHeight = totalRows * BUTTON_HEIGHT + (totalRows - 1) * VERTICAL_SPACING;

        // Рассчитываем позицию картинки
        Identifier MainText = Identifier.of("spmhelper", "titles/spmhelpertextmain.png");
        int originalWidth = 932/4;
        int originalHeight = 152/4;
        int imageWidth = Math.min(originalWidth, availableWidth);
        float scale = (float)imageWidth / originalWidth;
        int imageHeight = (int)(originalHeight * scale);

        // Общая высота блока (картинка + отступ + кнопки)
        int totalBlockHeight = imageHeight + IMAGE_BOTTOM_MARGIN + totalButtonsHeight;

        // Стартовая позиция для выравнивания по центру
        int startY = (height - totalBlockHeight) / 2;

        // Позиция первой строки кнопок
        int buttonsStartY = startY + imageHeight + IMAGE_BOTTOM_MARGIN;

        // Создаем кнопки с автоматическим переносом на новую строку и центрированием
        for (int i = 0; i < BUTTON_COUNT; i++) {
            int row = i / buttonsPerRow;
            int col = i % buttonsPerRow;

            int buttonsInCurrentRow = Math.min(buttonsPerRow, BUTTON_COUNT - row * buttonsPerRow);
            int rowWidth = buttonsInCurrentRow * buttonWidth + (buttonsInCurrentRow - 1) * HORIZONTAL_SPACING;
            int rowStartX = (width - rowWidth) / 2;

            int x = rowStartX + col * (buttonWidth + HORIZONTAL_SPACING);
            int y = buttonsStartY + row * (BUTTON_HEIGHT + VERTICAL_SPACING);

            final int buttonIndex = i;
            ButtonWidget button = ButtonWidget.builder(Text.of(buttonLabels[i]), (btn) -> {
                handleButtonClick(buttonIndex);
            }).dimensions(x, y, buttonWidth, BUTTON_HEIGHT).build();
            this.addDrawableChild(button);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Identifier MainText = Identifier.of("spmhelper", "titles/spmhelpertextmain.png");
        int originalWidth = 932/4;
        int originalHeight = 152/4;

        // Рассчитываем размер изображения
        int availableWidth = width - 2 * SCREEN_PADDING;
        int imageWidth = Math.min(originalWidth, availableWidth);
        float scale = (float)imageWidth / originalWidth;
        int imageHeight = (int)(originalHeight * scale);

        // Позиционируем изображение по центру сверху
        int totalRows = (int) Math.ceil((double) BUTTON_COUNT /
                Math.max(1, Math.min(BUTTON_COUNT,
                        availableWidth / (MIN_BUTTON_WIDTH + HORIZONTAL_SPACING))));
        int totalButtonsHeight = totalRows * BUTTON_HEIGHT + (totalRows - 1) * VERTICAL_SPACING;
        int totalBlockHeight = imageHeight + IMAGE_BOTTOM_MARGIN + totalButtonsHeight;
        int startY = (height - totalBlockHeight) / 2;

        int imageX = (width - imageWidth) / 2;
        int imageY = startY;

        context.drawTexture(MainText, imageX, imageY, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    private void handleButtonClick(int buttonLabel) {
        switch(buttonLabel) {
            case 0: this.client.setScreen(new PayScreen()); break;
            case 1: this.client.setScreen(new MapScreen()); break;
            case 2: this.client.setScreen(new Settings()); break; //Тут должен быть экран который открывает настройки или Settings или что-то ещё
            case 3: this.client.setScreen(new CallsScreen()); break;
            case 4: this.client.setScreen(new LawsScreen()); break;
        }
    }
}