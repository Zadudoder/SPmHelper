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
import zadudoder.spmhelper.utils.Misc;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.types.BaseCard;
import zadudoder.spmhelper.utils.types.Card;


@Environment(EnvType.CLIENT)
public class PayScreen extends Screen {
    private TextFieldWidget receiverCardOrNameField;
    private TextFieldWidget amountField;
    private TextFieldWidget commentField;
    private String p_number;
    private String p_amount;
    private String p_comment;
    private boolean isSPmPay = false;
    private String statusMessage;
    private int statusColor;
    private ButtonWidget selectCardButton;
    private ButtonWidget selectReceieverCardButton;
    private String selectedCard = null;
    private String selectedReceiverCard = null;
    private boolean cardsExpanded = false;
    private boolean receiverCardsExpanded = false;
    private ButtonWidget[] cardButtons;
    private ButtonWidget[] receiverCardButtons;

    private ButtonWidget cardNumberButton;
    private ButtonWidget nickNameButton;
    private String receiverName = "";

    public PayScreen() {
        super(Text.of("Перевод СПм"));
    }

    public PayScreen(String number, String amount, String comment) {
        super(Text.of("Перевод СПм"));
        p_number = number;
        p_amount = amount;
        p_comment = comment;
        isSPmPay = true;
        SPmHelperConfig.get().setPayWithNick(false);
    }

    public PayScreen(String number) {
        super(Text.of("Перевод СПм"));
        p_number = number;
        isSPmPay = true;
        SPmHelperConfig.get().setPayWithNick(false);
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

        // Выбор оплаты по нику или по карте

        cardNumberButton = ButtonWidget.builder(Text.of("Карта"), (btn) -> {
            SPmHelperConfig.get().setPayWithNick(false);
            cardNumberButton.active = false;
            nickNameButton.active = true;
            this.client.setScreen(new PayScreen());
        }).dimensions(width / 2 - 50, this.height / 2 - 80, 50, 20).build();

        nickNameButton = ButtonWidget.builder(Text.of("Ник"), (btn) -> {
            SPmHelperConfig.get().setPayWithNick(true);
            cardNumberButton.active = true;
            nickNameButton.active = false;
            this.client.setScreen(new PayScreen());
        }).dimensions(width / 2, this.height / 2 - 80, 50, 20).build();

        this.addDrawableChild(cardNumberButton);
        this.addDrawableChild(nickNameButton);

        cardNumberButton.active = SPmHelperConfig.get().paymentWithNick;
        nickNameButton.active = !SPmHelperConfig.get().paymentWithNick;

        int buttonWidth = 120;
        int buttonHeight = 20;
        int startY = this.height / 2;
        int centerX = this.width / 2 - buttonWidth / 2;

        if (selectedCard == null) {
            selectedCard = SPmHelperConfig.get().getMainCardName();
        }

        cardButtons = new ButtonWidget[SPmHelperConfig.get().getCards().size()];
        selectCardButton = addDrawableChild(ButtonWidget.builder(
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
        index = 0;
        // Чел выбирает по карте - false (по умолчанию), true - по нику, включается или сразу в настройках
        if (SPmHelperConfig.get().paymentWithNick) {
            selectReceieverCardButton = addDrawableChild(ButtonWidget.builder(
                    Text.translatable("text.spmhelper.pays_SelectСard"),
                    button -> toggleRecieverCards()
            ).dimensions(centerX + 170, startY - 50, buttonWidth, buttonHeight).build());
        }

        // Поле для номера карты получателя
        this.receiverCardOrNameField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 50,
                200, 20,
                Text.translatable("text.spmhelper.pays_CardNumber")
        );
        this.addDrawableChild(receiverCardOrNameField);

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
            receiverCardOrNameField.setText(p_number);
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
                setStatus(Text.translatable("text.spmhelper.pays_processTransfer_senderCardNull").getString(), 0xFF5555);
                return;
            }
            String receiverCardNumber;
            if(SPmHelperConfig.get().paymentWithNick){
                receiverCardNumber = selectedReceiverCard;
            } else {
                receiverCardNumber = receiverCardOrNameField.getText().trim();
            }
            if (receiverCardNumber.isEmpty()) {
                setStatus(Text.translatable("text.spmhelper.pays_processTransfer_receiverCardNumberNull").getString(), 0xFF5555);
                return;
            }
            if (amountField.getText().isEmpty()) {
                setStatus(Text.translatable("text.spmhelper.pays_processTransfer_amountFieldNull").getString(), 0xFF5555);
                return;
            }
            if(!Misc.isNumeric(amountField.getText())){
                setStatus(Text.translatable("text.spmhelper.pays_processTransfer_amountFieldRandomSymbol").getString(), 0xFF5555);
                return;
            }
            int amount = Integer.parseInt(amountField.getText());

            if (amount <= 0) {
                setStatus(Text.translatable("text.spmhelper.pays_processTransfer_amountField<0").getString(), 0xFF5555);
                return;
            }

            if (SPWorldsApi.getBalance(senderCard) < amount) {
                setStatus(Text.translatable("text.spmhelper.pays_processTransfer_Balance<Amount").getString(), 0xFF5555);
                return;
            }

            if ((MinecraftClient.getInstance().getSession().getUsername().length() + commentField.getText().length()) > 32) {
                setStatus (String.format(Text.translatable("text.spmhelper.pays_processTransfer_CommentIsLong").getString(), (30 - MinecraftClient.getInstance().getSession().getUsername().length())), 0xFF5555);
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
                    setStatus(Text.translatable("text.spmhelper.pays_processTransfer_ReceiverIsSender").getString(), 0xFF5555);
                } else if (error.contains("receiverCardNotFound")) {
                    setStatus(Text.translatable("text.spmhelper.pays_processTransfer_ReceiverCardNotFound").getString(), 0xFF5555);
                } else {
                    setStatus(String.format(Text.translatable("text.spmhelper.pays_processTransfer_ErrorAPI").getString(), response.get("error").getAsString()), 0xFF5555);
                }
            } else {
                setStatus(String.format(Text.translatable("text.spmhelper.pays.Successfully").getString(), amount), 0x55FF55);
            }

        } catch (Exception e) {
            setStatus(String.format(Text.translatable("text.spmhelper.pays_processTransfer_Error").getString(), e.getMessage()), 0xFF5555);
        }
    }

    private void loadSenderCard() {
        String cardName = SPmHelperConfig.get().getMainCardName();
        Card senderCard = SPmHelperConfig.get().getMainCard();

        JsonObject cardInfo = SPWorldsApi.getCardInfo(senderCard);

        if (cardInfo.has("error")) {
            setStatus(Text.translatable("text.spmhelper.pays_loadSenderCard_ErrorLoadingCard").getString(), 0xFF5555);
        } else {
            setStatus(String.format(Text.translatable("text.spmhelper.pays.CurrentBalance").getString(), cardName, cardInfo.get("balance").getAsString()), 0x55FF55);
        }
    }

    private void toggleCards() {
        if (!SPmHelperConfig.get().getCards().isEmpty()) {
            cardsExpanded = !cardsExpanded;
            updateCardsVisibility();
        } else {
            setStatus(Text.translatable("text.spmhelper.pays_toggleCards_NotLinkedCard").getString(), 0xFF5555);
        }

    }

    private void toggleRecieverCards() {
        if((receiverName.equals(receiverCardOrNameField.getText()) || receiverCardsExpanded) && !receiverCardOrNameField.getText().isEmpty()){
            receiverCardsExpanded = !receiverCardsExpanded;
            updateReceiverCardsVisibility();
        } else {
            if (SPmHelperConfig.get().getMainCardName().isEmpty()) {
                setStatus(Text.translatable("text.spmhelper.pays_toggleRecieverCards_LinkOneCard").getString(), 0xFF5555);
                return;
            }
            if (receiverCardOrNameField.getText().isEmpty()) {
                setStatus(Text.translatable("text.spmhelper.pays_toggleRecieverCards_PlayerNotEntry").getString(), 0xFF5555);
                return;
            }
            BaseCard[] cards = SPWorldsApi.getCards(receiverCardOrNameField.getText());
            if (cards == null || cards.length == 0) {
                setStatus(Text.translatable("text.spmhelper.pays_toggleRecieverCards_PlayerNotOrNoCard").getString(), 0xFF5555);
                return;
            }
            receiverName = receiverCardOrNameField.getText();
            int buttonWidth = 120;
            int buttonHeight = 20;
            int startY = this.height / 2;
            int centerX = this.width / 2 - buttonWidth / 2;
            receiverCardButtons = new ButtonWidget[cards.length];
            int index = 0;
            for (BaseCard card : cards) {
                receiverCardButtons[index] = ButtonWidget.builder(
                        Text.of(getCardButtonText(card)),
                        btn -> selectRecieverCard(card)
                ).dimensions(centerX + 170, startY - 25 + index * 25, buttonWidth, buttonHeight).build();
                receiverCardButtons[index].visible = cardsExpanded;
                this.addDrawableChild(receiverCardButtons[index]);
                index++;
            }
            receiverCardsExpanded = !receiverCardsExpanded;
            updateReceiverCardsVisibility();
        }
}


    private String getCardButtonText(String name) {
        return name + " | " + SPmHelperConfig.get().getCard(name).number;
    }
    private String getCardButtonText(BaseCard card){
        return  card.getName() + " | " + card.getNumber();
    }

    private void selectCard(String card) {
        SPmHelperConfig.get().setMainCard(card);
        setStatus(String.format(Text.translatable("text.spmhelper.pays.CurrentBalance").getString(), card, SPWorldsApi.getBalance(SPmHelperConfig.get().getMainCard())), 0x55FF55);
        cardsExpanded = false;
        updateCardsVisibility();
        selectedCard = card;
        selectCardButton.setMessage(Text.literal(getCardButtonText(selectedCard)));
    }

    private void selectRecieverCard(BaseCard card) {
        receiverCardsExpanded = false;
        updateReceiverCardsVisibility();
        selectedReceiverCard = card.getNumber();
        selectReceieverCardButton.setMessage(Text.literal(getCardButtonText(card)));
    }

    private void updateCardsVisibility() {
        for (ButtonWidget cardButton : cardButtons) {
            cardButton.visible = cardsExpanded;
            cardButton.active = !cardButton.getMessage().getString().equals(getCardButtonText(SPmHelperConfig.get().getMainCardName()));
        }
    }

    private void updateReceiverCardsVisibility() {
        for (ButtonWidget cardButton : receiverCardButtons) {
            cardButton.visible = receiverCardsExpanded;
            cardButton.active = !cardButton.getMessage().getString().equals(selectReceieverCardButton.getMessage().getString());
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
                Text.translatable(
                        SPmHelperConfig.get().paymentWithNick ? "text.spmhelper.pays_render_Nickname" : "text.spmhelper.pays_render_CardNumber"),
                this.width / 2 - 100,
                this.height / 2 - 60,
                0xA0A0A0,
                true
        );

        if (receiverCardOrNameField.getText().isEmpty()) {
            context.drawText(
                    textRenderer,
                    Text.translatable(
                            SPmHelperConfig.get().paymentWithNick ? "text.spmhelper.pays_render_ExampleNick" : "text.spmhelper.pays_render_Example"),
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
                    Text.of(statusMessage),
                    this.width / 2,
                    this.height / 2 + 80,
                    statusColor
            );
        }

        if (SPmHelperConfig.get().paymentWithNick) {
            context.drawText(
                    this.textRenderer,
                    Text.translatable("text.spmhelper.pays_render_CardWillComeAR"),
                    this.width / 2 + 109,
                    this.height / 2 - 60,
                    0xA0A0A0,
                    true
            );
        }

        Identifier PayText = Identifier.of("spmhelper", "titles/paystextrender.png");
        int imageY = height / 2 - 115;
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