package zadudoder.spmhelper.Screen.Pays;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import zadudoder.spmhelper.Screen.MainScreen;
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
    private ButtonWidget[] cardButtons;

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

        ButtonWidget Back = ButtonWidget.builder(Text.of("⬅"), (btn) -> {
            this.client.setScreen(new MainScreen());
        }).dimensions(5, 10, 15, 15).build();
        this.addDrawableChild(Back);


        int buttonWidth = 120;
        int buttonHeight = 20;
        int startY = this.height / 2;
        int centerX = this.width / 2 - buttonWidth / 2;

        if (selectedCard == null) {
            selectedCard = SPmHelperConfig.get().getMainCardName();
        }

        cardButtons = new ButtonWidget[SPmHelperConfig.get().getCards().size()];
        // Text.translatable("text.spmhelper.addCard_AcceptFeedBack")
        selectButton = addDrawableChild(ButtonWidget.builder(
                Text.translatable(!selectedCard.isEmpty() ? getCardButtonText(selectedCard) : "text.spmhelper.pays_SelectСard"),
                button -> toggleCards()
        ).dimensions(centerX - 170, startY - 50, buttonWidth, buttonHeight).build());

        // Кнопки карт (изначально скрыты)
        int index = 0;
        for (String name : SPmHelperConfig.get().getCards().keySet()) {
            cardButtons[index] = ButtonWidget.builder(
                    Text.of(getCardButtonText(name)),
                    btn -> selectCard(name)
            ).dimensions(centerX - 170, startY - 25 + index * 25, buttonWidth, buttonHeight).build();
            cardButtons[index].visible = cardsExpanded;
            this.addDrawableChild(cardButtons[index]);
            index++;
        }


        // Поле для номера карты получателя
        this.receiverCardField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 50,
                200, 20,
                Text.translatable("text.spmhelper.pays_CardNumber")
        );
        this.addDrawableChild(receiverCardField);

        // Поле для суммы перевода
        this.amountField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 15,
                200, 20,
                Text.translatable("text.spmhelper.pays_Amount")
        );
        this.addDrawableChild(amountField);

        // Поле для комментария
        this.commentField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 + 20,
                200, 20,
                Text.translatable("text.spmhelper.pays_Comment")
        );
        this.addDrawableChild(commentField);
        if (isSPmPay) {
            receiverCardField.setText(p_number);
            amountField.setText(p_amount);
            commentField.setText(p_comment);
        }

        // Кнопка перевода
        ButtonWidget transferButton = ButtonWidget.builder(Text.translatable("text.spmhelper.pays_Transfer"), button -> {
                    processTransfer();
                })
                .dimensions(this.width / 2 - 100, this.height / 2 + 50, 200, 20)
                .build();
        this.addDrawableChild(transferButton);
    }


    private void processTransfer() {
        try {
            Card senderCard = SPmHelperConfig.get().getMainCard();
            if (senderCard == null) {
                setStatus("text.spmhelper.pays_processTransfer_senderCardNull", 0xFF5555);
                return;
            }

            String receiverCardNumber = receiverCardField.getText().trim();
            if (receiverCardNumber.isEmpty()) {
                setStatus("text.spmhelper.pays_processTransfer_receiverCardNumberNull", 0xFF5555);
                return;
            }
            if (amountField.getText().isEmpty()) {
                setStatus("text.spmhelper.pays_processTransfer_amountFieldNull", 0xFF5555);
                return;
            }
            int amount;
            try { //переписать
                amount = Integer.parseInt(amountField.getText());
            } catch (NumberFormatException ex) {
                setStatus("text.spmhelper.pays_processTransfer_amountFieldRandomSymbol", 0xFF5555);
                return;
            }

            if (amount <= 0) {
                setStatus("text.spmhelper.pays_processTransfer_amountField<0", 0xFF5555);
                return;
            }

            if (SPWorldsApi.getBalance(senderCard) < amount) {
                setStatus("text.spmhelper.pays_processTransfer_Balance<Amount", 0xFF5555);
                return;
            }

            if ((MinecraftClient.getInstance().getSession().getUsername().length() + commentField.getText().length()) > 32) {
                setStatus("text.spmhelper.pays_processTransfer_CommentIsLong" + (30 - MinecraftClient.getInstance().getSession().getUsername().length()), 0xFF5555);
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
                    setStatus("text.spmhelper.pays_processTransfer_ReceiverIsSender", 0xFF5555);
                } else if (error.contains("receiverCardNotFound")) {
                    setStatus("text.spmhelper.pays_processTransfer_ReceiverCardNotFound", 0xFF5555);
                } else {
                    setStatus("text.spmhelper.pays_processTransfer_ErrorAPI" + response.get("error").getAsString(), 0xFF5555);
                }
            } else {
                setStatus("✔ Успешно переведено " + amount + " АР", 0x55FF55);
            }

        } catch (Exception e) {
            setStatus("text.spmhelper.pays_processTransfer_Error" + e.getMessage(), 0xFF5555);
        }
    }

    private void loadSenderCard() {
        String cardName = SPmHelperConfig.get().getMainCardName();
        Card senderCard = SPmHelperConfig.get().getMainCard();

        JsonObject cardInfo = SPWorldsApi.getCardInfo(senderCard);

        if (cardInfo.has("error")) {
            setStatus("text.spmhelper.pays_loadSenderCard_ErrorLoadingCard", 0xFF5555);
        } else {
            setStatus("text.spmhelper.pays.CurrentBalance" + cardName + "\": " + cardInfo.get("balance").getAsString() + "text.spmhelper.pays_DiamondOre", 0x55FF55);
        }
    }

    private void toggleCards() {
        if (!SPmHelperConfig.get().getCards().isEmpty()) {
            cardsExpanded = !cardsExpanded;
            updateCardsVisibility();
        } else {
            setStatus("text.spmhelper.pays_toggleCards_NotLinkedCard", 0xFF5555);
        }

    }

    private String getCardButtonText(String name) {
        return name + " | " + SPmHelperConfig.get().getCard(name).number;
    }

    private void selectCard(String card) {
        SPmHelperConfig.get().setMainCard(card);
        setStatus("text.spmhelper.pays.CurrentBalance" + card + "\": " + SPWorldsApi.getBalance(SPmHelperConfig.get().getMainCard()) + "text.spmhelper.pays_DiamondOre", 0x55FF55);
        cardsExpanded = false;
        updateCardsVisibility();
        selectedCard = card;
        selectButton.setMessage(Text.literal(getCardButtonText(selectedCard)));
    }

    private void updateCardsVisibility() {
        for (ButtonWidget cardButton : cardButtons) {
            cardButton.visible = cardsExpanded;
            cardButton.active = !cardButton.getMessage().getString().equals(getCardButtonText(SPmHelperConfig.get().getMainCardName()));
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
                Text.translatable("text.spmhelper.pays_render_CardForPayment"),
                this.width / 2 - 229,
                this.height / 2 - 60,
                0xA0A0A0,
                true
        );

        // Подписи к полям
        context.drawText(
                this.textRenderer,
                Text.translatable("text.spmhelper.pays_render_CardNumber"),
                this.width / 2 - 100,
                this.height / 2 - 60,
                0xA0A0A0,
                true
        );

        if (receiverCardField.getText().isEmpty()) {
            context.drawText(
                    textRenderer,
                    Text.translatable("text.spmhelper.pays_render_Example"),
                    width / 2 - 95,
                    height / 2 - 44,
                    0xbbbbbb,
                    false
            );
        }

        context.drawText(
                this.textRenderer,
                Text.translatable("text.spmhelper.pays_render_AmountAR"),
                this.width / 2 - 100,
                this.height / 2 - 25,
                0xA0A0A0,
                true
        );

        if (amountField.getText().isEmpty()) {
            context.drawText(
                    textRenderer,
                    Text.translatable("text.spmhelper.pays_render_AmountMinAndMax"),
                    width / 2 - 95,
                    height / 2 - 9,
                    0xbbbbbb,
                    false
            );
        }

        context.drawText(
                this.textRenderer,
                Text.translatable("text.spmhelper.pays_render_Comment"),
                this.width / 2 - 100,
                this.height / 2 + 10,
                0xA0A0A0,
                true
        );

        if (commentField.getText().isEmpty()) {
            context.drawText(
                    textRenderer,
                    Text.translatable("text.spmhelper.pays_render_CommentMayBeNull"),
                    width / 2 - 95,
                    height / 2 + 26,
                    0xbbbbbb,
                    false
            );
        }
        if (statusMessage != null) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable(statusMessage),
                    this.width / 2,
                    this.height / 2 + 80,
                    statusColor
            );
        }

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