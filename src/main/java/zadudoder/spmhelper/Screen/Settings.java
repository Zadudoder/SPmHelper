package zadudoder.spmhelper.Screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public class Settings extends Screen {
    private boolean cardsExpanded = false;
    private final List<String> testCards = Arrays.asList("Хайл карта", "Тест карта");
    private String selectedCard = null;
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

        // Основная кнопка выбора карт
        this.addDrawableChild(ButtonWidget.builder(
                Text.of(selectedCard != null ? selectedCard : "Выберите карты ⬇"),
                button -> toggleCards()
        ).dimensions(centerX - 80, startY - 50, buttonWidth, buttonHeight).build());

        // Кнопки карт (изначально скрыты)
        for (int i = 0; i < testCards.size(); i++) {
            String card = testCards.get(i);
            ButtonWidget cardBtn = ButtonWidget.builder(
                    Text.of(card),
                    btn -> selectCard(card)
            ).dimensions(centerX - 80, startY - 50 + (i + 1) * 25, buttonWidth, buttonHeight).build();
            cardBtn.visible = cardsExpanded;
            this.addDrawableChild(cardBtn);
        }

        // Ваши оригинальные кнопки управления
        this.addDrawableChild(ButtonWidget.builder(Text.of("Удалить"), button -> {
            if (selectedCard != null) {
                // Логика удаления
            }
        }).dimensions(centerX + 80, startY - 50, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Изменить имя"), button -> {
            if (selectedCard != null) {
                // Логика изменения имени
            }
        }).dimensions(centerX + 80, startY - 25, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Выбрать для оплаты"), button -> {
            if (selectedCard != null) {
                // Логика выбора для оплаты
            }
        }).dimensions(centerX + 80, startY, buttonWidth, buttonHeight).build());

        // Кнопки внизу экрана
        this.addDrawableChild(ButtonWidget.builder(Text.of("Сохранить"), button -> {
            // Логика сохранения
        }).dimensions(centerX + 80, startY + 50, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Авторизоваться"), button -> {
            // Логика авторизации
        }).dimensions(centerX - 15, startY + 80, buttonWidth + 30, buttonHeight).build());
    }

    private void toggleCards() {
        cardsExpanded = !cardsExpanded;
        updateCardsVisibility();
    }

    private void selectCard(String card) {
        selectedCard = card;
        cardsExpanded = false;
        updateCardsVisibility();
        this.clearAndInit(); // Пересоздаем экран чтобы обновить текст кнопки
    }

    private void updateCardsVisibility() {
        for (int i = 0; i < testCards.size(); i++) {
            int btnIndex = 1 + i; // 0 - основная кнопка
            if (btnIndex < this.children().size()) {
                ClickableWidget btn = (ClickableWidget) this.children().get(btnIndex);
                btn.visible = cardsExpanded;
            }
        }
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