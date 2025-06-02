package zadudoder.spmhelper.Screen.Pays;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.types.Card;

@Environment(EnvType.CLIENT)
public class PayScreen extends Screen {
    private TextFieldWidget receiverCardField;
    private TextFieldWidget amountField;
    private TextFieldWidget commentField;
    private String p_number;
    private String p_amount;
    private String p_comment;
    private boolean isSPmPay = false;
    private String statusMessage;
    private int statusColor;
    private ButtonWidget selectButton;
    private String selectedCard = null;
    private boolean cardsExpanded = false;

    public PayScreen() {
        super(Text.of("Перевод СПм"));
    }

    public PayScreen(String number, String amount, String comment) {
        super(Text.of("Перевод СПм"));
        p_number = number;
        p_amount = amount;
        p_comment = comment;
        isSPmPay = true;
    }

    @Override
    protected void init() {
        super.init();
        loadSenderCard();
        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spmhelper.ru");
        }).dimensions(width - 20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);


        int buttonWidth = 120;
        int buttonHeight = 20;
        int startY = this.height / 2;
        int centerX = this.width / 2 - buttonWidth / 2;

        // Основная кнопка выбора карт
        /*selectButton = addDrawableChild(ButtonWidget.builder(
                Text.of(selectedCard != null ? selectedCard : "Выберите карту ⬇"),
                button -> toggleCards()
        ).dimensions(width / 2 - 240, height / 2 - 15, buttonWidth, buttonHeight).build());

        // Кнопки карт (изначально скрыты)
        int index = 0;
        for (String name : SPmHelperConfig.get().getCards().keySet()) {
            index++;
            ButtonWidget cardBtn = ButtonWidget.builder(
                    Text.of(name),
                    btn -> selectCard(name)
            ).dimensions(centerX - 80, startY - 50 + index * 25, buttonWidth, buttonHeight).build();
            cardBtn.visible = cardsExpanded;
            this.addDrawableChild(cardBtn);
        }*/


        // Поле для номера карты получателя
        this.receiverCardField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 50,
                200, 20,
                Text.of("Номер карты получателя")
        );
        this.addDrawableChild(receiverCardField);

        // Поле для суммы перевода
        this.amountField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 15,
                200, 20,
                Text.of("Сумма (АР):")
        );
        this.addDrawableChild(amountField);

        // Поле для комментария
        this.commentField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 + 20,
                200, 20,
                Text.of("Комментарий (не обязательно)")
        );
        this.addDrawableChild(commentField);
        if (isSPmPay) {
            receiverCardField.setText(p_number);
            amountField.setText(p_amount);
            commentField.setText(p_comment);
        }

        // Кнопка перевода
        ButtonWidget transferButton = ButtonWidget.builder(Text.of("Перевести"), button -> {
                    processTransfer();
                })
                .dimensions(this.width / 2 - 100, this.height / 2 + 50, 200, 20)
                .build();
        this.addDrawableChild(transferButton);
    }

    /*private void toggleCards() {
        cardsExpanded = !cardsExpanded;
        updateCardsVisibility();
    }

    private void updateCardsVisibility() {
        int index = 0;
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget && element != selectButton) {
                index++;
                if (index > 0 && index <= SPmHelperConfig.get().getCards().size()) {
                    widget.visible = cardsExpanded;
                }
            }
        }
    }

    private void selectCard(String card) {
        selectedCard = card;
        cardsExpanded = false;
        updateCardsVisibility();
        // Не вызываем clearAndInit(), чтобы не сбрасывать состояние
        selectButton.setMessage(Text.of(card));
    }*/

    private void processTransfer() {
        try {
            Card senderCard = SPmHelperConfig.get().getMainCard();
            if (senderCard == null) {
                setStatus("❌ Укажите карту для оплаты", 0xFF5555);
                return;
            }

            String receiverCardNumber = receiverCardField.getText().trim();
            if (receiverCardNumber.isEmpty()) {
                setStatus("❌ Введите номер карты получателя", 0xFF5555);
                return;
            }
            if (amountField.getText().isEmpty()) {
                setStatus("❌ Сумма не указана", 0xFF5555);
                return;
            }
            int amount;
            try { //переписать
                amount = Integer.parseInt(amountField.getText());
            } catch (NumberFormatException ex) {
                setStatus("❌ Сумма не указана", 0xFF5555);
                return;
            }

            if (amount <= 0) {
                setStatus("❌ Сумма должна быть больше 0", 0xFF5555);
                return;
            }

            if (SPWorldsApi.getBalance(senderCard) < amount) {
                setStatus("❌ У вас не достаточно АР на карте", 0xFF5555);
                return;
            }

            if ((MinecraftClient.getInstance().getSession().getUsername().length() + commentField.getText().length()) > 32) {
                setStatus("❌ Слишком длинный комментарий, максимум: " + (30 - MinecraftClient.getInstance().getSession().getUsername().length()), 0xFF5555);
                return;
            }

            // Создаем перевод: senderCard -> receiverCard
            JsonObject response = SPWorldsApi.createTransfer(
                    senderCard,
                    receiverCardNumber,
                    amount,
                    commentField.getText()
            );
            if (response.has("error")) {
                String error = response.get("error").toString();
                if (error.contains("receiverIsSender")) {
                    setStatus("❌ Нельзя отправить деньги самому себе", 0xFF5555);
                } else if (error.contains("receiverCardNotFound")) {
                    setStatus("❌ Такой карты не существует", 0xFF5555);
                } else {
                    setStatus("❌ Ошибка API: " + response.get("error").getAsString(), 0xFF5555);
                }
            } else {
                setStatus("✔ Успешно переведено " + amount + " АР", 0x55FF55);
            }

        } catch (Exception e) {
            setStatus("❌ Ошибка: " + e.getMessage(), 0xFF5555);
        }
    }

    private void loadSenderCard() {
        Card senderCard = SPmHelperConfig.get().getMainCard();
        String cardName = SPmHelperConfig.get().getMainCardName();

        if (senderCard == null) {
            setStatus("❌ Вы не указали карту для оплаты", 0xFF5555);
            return;
        }
        JsonObject cardInfo = SPWorldsApi.getCardInfo(senderCard);

        if (cardInfo.has("error")) {
            setStatus("❌ Ошибка загрузки карты", 0xFF5555);
        } else {
            setStatus("Текущий баланс карты \""+ cardName +"\": "+ cardInfo.get("balance").getAsString() + " АР", 0x55FF55);
        }
    }

    private void setStatus(String message, int color) {
        this.statusMessage = message;
        this.statusColor = color;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);


        // Подписи к полям
        context.drawText(
                this.textRenderer,
                Text.of("Номер карты:"),
                this.width / 2 - 100,
                this.height / 2 - 60,
                0xA0A0A0,
                true
        );

        if (receiverCardField.getText().isEmpty()) {
            context.drawText(
                    textRenderer,
                    "Пример: 00001",
                    width / 2 - 95,
                    height / 2 - 44,
                    0xbbbbbb,
                    false
            );
        }

        context.drawText(
                this.textRenderer,
                Text.of("Сумма (АР):"),
                this.width / 2 - 100,
                this.height / 2 - 25,
                0xA0A0A0,
                true
        );

        if (amountField.getText().isEmpty()) {
            context.drawText(
                    textRenderer,
                    "От 1 до 10000",
                    width / 2 - 95,
                    height / 2 - 9,
                    0xbbbbbb,
                    false
            );
        }

        context.drawText(
                this.textRenderer,
                Text.of("Комментарий:"),
                this.width / 2 - 100,
                this.height / 2 + 10,
                0xA0A0A0,
                true
        );

        if (commentField.getText().isEmpty()) {
            context.drawText(
                    textRenderer,
                    "Можно оставить пустым",
                    width / 2 - 95,
                    height / 2 + 26,
                    0xbbbbbb,
                    false
            );
        }

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of(statusMessage),
                this.width / 2,
                this.height / 2 + 80,
                statusColor
        );

        Identifier PayText = Identifier.of("spmhelper", "titles/paystextrender.png");
        int imageY = height / 2 - 110;
        int originalWidth = 674 / 4;
        int originalHeight = 123 / 4;
        int availableWidth = width - 40;
        int finalWidth = originalWidth;
        int finalHeight = originalHeight;
        if (originalWidth > availableWidth) {
            float scale = (float) availableWidth / originalWidth;
            finalWidth = availableWidth;
            finalHeight = (int) (originalHeight * scale);
        }
        int imageX = (width - finalWidth) / 2;
        context.drawTexture(PayText, imageX, imageY, 0, 0, finalWidth, finalHeight, finalWidth, finalHeight);

    }
}