package zadudoder.spmhelper.Screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import zadudoder.spmhelper.SPmHelperClient;
import zadudoder.spmhelper.utils.SPmHelperApi;

public class Settings extends Screen {
    private static final Identifier SETTINGS_TEXT = Identifier.of("spmhelper", "titles/settingstextrender.png");
    private boolean cardsExpanded = false;
    private boolean hasToken;
    private String selectedCard = null;
    private String statusMessage;
    private int statusColor;

    public Settings() {
        super(Text.of("Экран настроек"));
    }

    @Override
    protected void init() {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int startY = this.height / 2;
        int centerX = this.width / 2 - buttonWidth / 2;
        this.hasToken = SPmHelperClient.config.getAPI_TOKEN() != null && !SPmHelperClient.config.getAPI_TOKEN().isEmpty();

        // Основная кнопка выбора карт
        ButtonWidget selectButton =
                addDrawableChild(ButtonWidget.builder(
                        Text.of(selectedCard != null ? selectedCard : "Выберите карту ⬇"),
                        button -> toggleCards()
                ).dimensions(centerX - 80, startY - 50, buttonWidth, buttonHeight).build());

        // Кнопки карт (изначально скрыты)
        int index = 0;
        for (String name : SPmHelperClient.config.getCards().keySet()) {
            index++;
            ButtonWidget cardBtn = ButtonWidget.builder(
                    Text.of(name),
                    btn -> selectCard(name)
            ).dimensions(centerX - 80, startY - 50 + index * 25, buttonWidth, buttonHeight).build();
            cardBtn.visible = cardsExpanded;
            this.addDrawableChild(cardBtn);
        }

        // Ваши оригинальные кнопки управления
        this.addDrawableChild(ButtonWidget.builder(Text.of("Удалить"), button -> {
            if (selectedCard != null) {
                SPmHelperClient.config.removeCard(selectedCard);
                selectedCard = null;
                this.clearAndInit();
                selectButton.setMessage(Text.literal("Выберите карту ⬇"));
                setStatus("✔ Карта успешно удалена!", 0x55FF55);
            }
        }).dimensions(centerX + 80, startY - 50, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Изменить имя"), button -> {
            if (selectedCard != null) {
                String newName = "Новое имя(заглушка)";
                SPmHelperClient.config.renameCard(selectedCard, newName);
                selectedCard = newName;
                this.clearAndInit();
                selectButton.setMessage(Text.literal(newName));
                setStatus("✔ Имя карты успешно изменено", 0x55FF55);
            }
        }).dimensions(centerX + 80, startY - 25, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Выбрать для оплаты"), button -> {
            if (selectedCard != null) {
                SPmHelperClient.config.setMainCard(selectedCard);
                setStatus("✔ Карта выбрана для оплаты!", 0x55FF55);
            }
        }).dimensions(centerX + 80, startY, buttonWidth, buttonHeight).build());

        // Кнопки внизу экрана
        this.addDrawableChild(ButtonWidget.builder(Text.of("Главное меню"), button -> {
            this.client.setScreen(new MainScreen());
        }).dimensions(centerX + 80, startY + 50, buttonWidth, buttonHeight).build());

        if (!hasToken) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("Авторизоваться"), button -> {
                SPmHelperApi.startAuthProcess(MinecraftClient.getInstance().player);
                this.close();
            }).dimensions(centerX - 15, startY + 80, buttonWidth + 30, buttonHeight).build());
        }
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
        int index = 0;
        for (String name : SPmHelperClient.config.getCards().keySet()) {
            index++;
            int btnIndex = index; // 0 - основная кнопка
            if (btnIndex < this.children().size()) {
                ClickableWidget btn = (ClickableWidget) this.children().get(btnIndex);
                btn.visible = cardsExpanded;
            }
        }
    }

    private void setStatus(String message, int color) {
        this.statusMessage = message;
        this.statusColor = color;
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

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of(statusMessage),
                this.width / 2,
                this.height / 2 + 80,
                statusColor
        );
    }
}