package zadudoder.spmhelper.Screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Settings extends Screen {
    private static final Identifier SETTINGS_TEXT = Identifier.of("spmhelper", "titles/settingstextrender.png");

    public Settings() {
        super(Text.of("Экран настроек"));
    }

    @Override
    protected void init() {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int startY = this.height / 2;
        int centerX = this.width / 2 - buttonWidth / 2;

        // Кнопки управления
        this.addDrawableChild(ButtonWidget.builder(Text.of("Удалить"), button -> {
            // Логика удаления карты
        }).dimensions(centerX+80, startY - 50, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Изменить имя"), button -> {
            // Логика изменения имени
        }).dimensions(centerX+80, startY - 25, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Выбрать для оплаты"), button -> {
            // Логика выбора карты
        }).dimensions(centerX+80, startY, buttonWidth, buttonHeight).build());

        // Кнопки внизу
        this.addDrawableChild(ButtonWidget.builder(Text.of("Сохранить"), button -> {
            // Логика сохранения
        }).dimensions(centerX+80, startY + 50, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Авторизоваться в моде"), button -> {
            // Логика авторизации
        }).dimensions(centerX-15, startY + 100, buttonWidth+30, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Рендер заголовка "НАСТРОЙКИ"
        int imageY = height / 2 - 110;
        int originalWidth = 932 / 4;
        int originalHeight = 179 / 4;
        int availableWidth = width - 40;
        int finalWidth = originalWidth;
        int finalHeight = originalHeight;

        if (originalWidth > availableWidth) {
            float scale = (float) availableWidth / originalWidth;
            finalWidth = availableWidth;
            finalHeight = (int) (originalHeight * scale);
        }

        int imageX = (width - finalWidth) / 2;
        context.drawTexture(SETTINGS_TEXT, imageX, imageY, 0, 0, finalWidth, finalHeight, finalWidth, finalHeight);
    }
}