package zadudoder.spmhelper.Screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.SPmHelperApi;

public class Settings extends Screen {
    private static final Identifier SETTINGS_TEXT = Identifier.of("spmhelper", "titles/settingstextrender.png");
    private boolean cardsExpanded = false;
    private boolean hasToken;
    private String selectedCard = null;
    private String statusMessage;
    private TextFieldWidget newNameCard;
    private int statusColor;
    private ButtonWidget selectButton;
    private ButtonWidget[] cardButtons;

    public Settings() {
        super(Text.of("Экран настроек"));
    }

    @Override
    protected void init() {

        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spmhelper.ru");
        }).dimensions(width - 20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);

        ButtonWidget Back = ButtonWidget.builder(Text.of("⬅"), (btn) -> {
            this.client.setScreen(new MainScreen());
        }).dimensions(5, 10, 15, 15).build();
        this.addDrawableChild(Back);

        int buttonWidth = 120;
        int buttonHeight = 20;
        int startY = this.height / 2;
        int centerX = this.width / 2 - buttonWidth / 2;
        this.hasToken = SPmHelperConfig.get().getAPI_TOKEN() != null && !SPmHelperConfig.get().getAPI_TOKEN().isEmpty();
        cardButtons = new ButtonWidget[SPmHelperConfig.get().getCards().size()];
        // Основная кнопка выбора карт
        selectButton = addDrawableChild(ButtonWidget.builder(
                Text.of("Выберите карту ⬇"),
                button -> toggleCards()
        ).dimensions(centerX - 80, startY - 50, buttonWidth, buttonHeight).build());

        // Кнопки карт (изначально скрыты)
        int index = 0;
        for (String name : SPmHelperConfig.get().getCards().keySet()) {
            cardButtons[index] = ButtonWidget.builder(
                    Text.of(getCardButtonText(name)),
                    btn -> selectCard(name)
            ).dimensions(centerX - 80, startY - 25 + index * 25, buttonWidth, buttonHeight).build();
            cardButtons[index].visible = cardsExpanded;
            this.addDrawableChild(cardButtons[index]);
            index++;
        }

        // Кнопка удаления
        this.addDrawableChild(ButtonWidget.builder(Text.of("Удалить"), button -> {
            if (selectedCard != null) {
                SPmHelperConfig.get().removeCard(selectedCard);
                selectedCard = null;
                reloadScreen();
                setStatus("✔ Карта успешно удалена!", 0x55FF55);

            } else {
                setStatus("Сначала выберите карту!", 0xFFFF00);
            }
        }).dimensions(centerX + 80, startY - 50, buttonWidth, buttonHeight).build());

        // Поле для нового имени и кнопки подтверждения/отмены
        newNameCard = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 75,
                this.height / 2 + 30,
                150, 20,
                Text.of("Введите новое имя")
        );
        newNameCard.setVisible(false);
        this.addDrawableChild(newNameCard);

        ButtonWidget newNameCardAccept = ButtonWidget.builder(Text.of("✔"), button -> {
            String newName = newNameCard.getText().trim();
            if (!newName.isEmpty()) {
                for (String name : SPmHelperConfig.get().getCards().keySet()) {
                    if (name.equals(newName)) {
                        setStatus("❌ Данное имя карты уже используется", 0xFFFF00);
                        return;
                    }
                    if (newName.length() > 12) {
                        setStatus("❌ Имя не должно быть длиннее 12 символов",0xFFFF00);
                        return;
                    }
                }
                SPmHelperConfig.get().renameCard(selectedCard, newName);
                selectedCard = null;
                reloadScreen();
                setStatus("✔ Имя карты успешно изменено на " + newName, 0x55FF55);
            }
        }).dimensions(width / 2 + 85, height / 2 + 30, 20, 20).build();
        newNameCardAccept.visible = false;
        this.addDrawableChild(newNameCardAccept);

        ButtonWidget newNameCardCancel = ButtonWidget.builder(Text.of("❌"), button -> {
            reloadScreen();
            clearStatus();
        }).dimensions(width / 2 - 105, height / 2 + 30, 20, 20).build();
        newNameCardCancel.visible = false;
        this.addDrawableChild(newNameCardCancel);

        // Кнопка изменения имени
        this.addDrawableChild(ButtonWidget.builder(Text.of("Изменить имя"), button -> {
            if (selectedCard != null) {
                newNameCard.setVisible(true);
                newNameCardAccept.visible = true;
                newNameCardCancel.visible = true;
                setStatus("⬆ Введите новое имя в появившемся окне ⬆", 0xBBBBBB);
            } else {
                setStatus("Сначала выберите карту!", 0xFFFF00);
            }
        }).dimensions(centerX + 80, startY - 25, buttonWidth, buttonHeight).build());

        // Кнопка выбора для оплаты
        this.addDrawableChild(ButtonWidget.builder(Text.of("Выбрать для оплаты"), button -> {
            if (selectedCard != null) {
                SPmHelperConfig.get().setMainCard(selectedCard);
                setStatus("✔ \"" + SPmHelperConfig.get().getMainCardName() + "\" выбрана для оплаты!", 0x55FF55);
                reloadScreen();
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
        if (!SPmHelperConfig.get().getCards().isEmpty()) {
            cardsExpanded = !cardsExpanded;
            updateCardsVisibility();
        } else {
            setStatus("Нет привязанных карт!", 0xFF5555);
        }

    }

    private void reloadScreen() {
        cardsExpanded = false;
        this.clearAndInit();
    }

    private String getCardButtonText(String name) {
        return name + " | " + SPmHelperConfig.get().getCard(name).number;
    }

    private void selectCard(String card) {
        selectedCard = card;
        cardsExpanded = false;
        updateCardsVisibility();
        selectButton.setMessage(Text.literal(getCardButtonText(selectedCard)));
    }

    private void updateCardsVisibility() {
        for (ButtonWidget cardButton : cardButtons) {
            cardButton.visible = cardsExpanded;
        }
    }

    private void setStatus(String message, int color) {
        this.statusMessage = message;
        this.statusColor = color;
    }

    private void clearStatus() {
        this.statusMessage = "";
        this.statusColor = 0xFFFFFF;
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

        int y;
        if (hasToken) {
            y = height / 2 + 80;
        } else {
            y = height / 2 + 110;
        }

        // Отрисовать статус
        if (statusMessage != null) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.of(statusMessage),
                    this.width / 2,
                    y,
                    statusColor
            );
        }
    }
}