package zadudoder.spmhelper.Screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import zadudoder.spmhelper.SPmHelperClient;
import zadudoder.spmhelper.utils.SPmHelperApi;

public class Settings extends Screen {
    private static final Identifier SETTINGS_TEXT = Identifier.of("spmhelper", "titles/settingstextrender.png");
    private boolean cardsExpanded = false;
    private boolean hasToken;
    private boolean visiblebutton = false;
    private String selectedCard = null;
    private String statusMessage;
    private TextFieldWidget NewNameCard;
    private ButtonWidget NewNameCardAccept;
    private ButtonWidget NewNameCardCancel;
    private int statusColor;
    private int Y;
    private ButtonWidget selectButton; // Сохраняем ссылку на кнопку выбора

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
        selectButton = addDrawableChild(ButtonWidget.builder(
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

        // Кнопка удаления
        this.addDrawableChild(ButtonWidget.builder(Text.of("Удалить"), button -> {
            if (selectedCard != null) {
                SPmHelperClient.config.removeCard(selectedCard);
                selectedCard = null;
                visiblebutton = false;
                this.clearAndInit();
                setStatus("✔ Карта успешно удалена!", 0x55FF55);
            } else {
                setStatus("Сначала выберите карту!", 0xFFFF00);
            }
        }).dimensions(centerX + 80, startY - 50, buttonWidth, buttonHeight).build());

        // Поле для нового имени и кнопки подтверждения/отмены
        NewNameCard = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 75,
                this.height / 2 + 30,
                150, 20,
                Text.of("Введите новое имя")
        );
        NewNameCard.setVisible(visiblebutton);
        this.addDrawableChild(NewNameCard);

        NewNameCardAccept = ButtonWidget.builder(Text.of("✔"), button -> {
            String newName = NewNameCard.getText().trim();
            if (!newName.isEmpty()) {
                SPmHelperClient.config.renameCard(selectedCard, newName);
                selectedCard = newName;
                visiblebutton = false;
                this.clearAndInit();
                setStatus("✔ Имя карты успешно изменено на: " + newName, 0x55FF55);
            }
        }).dimensions(centerX + 105, height / 2 + 30, 20, 20).build();
        NewNameCardAccept.visible = visiblebutton;
        this.addDrawableChild(NewNameCardAccept);

        NewNameCardCancel = ButtonWidget.builder(Text.of("❌"), button -> {
            visiblebutton = false;
            this.clearAndInit();
        }).dimensions(centerX - 105, height / 2 + 30, 20, 20).build();
        NewNameCardCancel.visible = visiblebutton;
        this.addDrawableChild(NewNameCardCancel);

        // Кнопка изменения имени
        this.addDrawableChild(ButtonWidget.builder(Text.of("Изменить имя"), button -> {
            if (selectedCard != null) {
                visiblebutton = true;
                this.clearAndInit(); // Пересоздаем экран с новым состоянием
            } else {
                setStatus("Сначала выберите карту!", 0xFFFF00);
            }
        }).dimensions(centerX + 80, startY - 25, buttonWidth, buttonHeight).build());

        // Кнопка выбора для оплаты
        this.addDrawableChild(ButtonWidget.builder(Text.of("Выбрать для оплаты"), button -> {
            if (selectedCard != null) {
                SPmHelperClient.config.setMainCard(selectedCard);
                setStatus("✔ Карта выбрана для оплаты!", 0x55FF55);
            } else {
                setStatus("Сначала выберите карту!", 0xFFFF00);
            }
        }).dimensions(centerX + 80, startY, buttonWidth, buttonHeight).build());

        // Кнопка авторизации
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
        // Не вызываем clearAndInit(), чтобы не сбрасывать состояние
        selectButton.setMessage(Text.of(card));
    }

    private void updateCardsVisibility() {
        int index = 0;
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget && element != selectButton) {
                ClickableWidget widget = (ClickableWidget)element;
                index++;
                if (index > 0 && index <= SPmHelperClient.config.getCards().size()) {
                    widget.visible = cardsExpanded;
                }
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

        if (hasToken) {
            Y = height / 2 + 80;
        } else {
            Y = height / 2 + 110;
        }

        // Отрисовать статус
        if (statusMessage != null) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.of(statusMessage),
                    this.width / 2,
                    Y,
                    statusColor
            );
        }

        if (visiblebutton && NewNameCard != null && NewNameCard.getText().isEmpty()) {
            context.drawText(textRenderer, "Введите новое имя",
                    width / 2 - 70, height / 2 + 36, 0xBBBBBB, false);
        }
    }
}