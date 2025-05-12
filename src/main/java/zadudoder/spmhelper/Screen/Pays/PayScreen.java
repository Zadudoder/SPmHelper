package zadudoder.spmhelper.Screen.Pays;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import zadudoder.spmhelper.SPmHelper;
import zadudoder.spmhelper.SPmHelperClient;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.types.Card;

public class PayScreen extends Screen {
    private TextFieldWidget receiverCardField;
    private TextFieldWidget amountField;
    private TextFieldWidget commentField;
    private String statusMessage;
    private int statusColor;

    public PayScreen() {
        super(Text.of("Перевод СП"));
    }

    @Override
    protected void init() {
        super.init();
        loadSenderCard();
        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spworlds.ru/spm/groups/06c25d05-b370-47d4-8416-fa1011ea69a1");
        }).dimensions(width - 20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);

        // Поле для номера карты получателя
        this.receiverCardField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 70,
                200, 20,
                Text.of("Номер карты получателя")
        );
        this.addDrawableChild(receiverCardField);

        // Поле для суммы перевода
        this.amountField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 35,
                200, 20,
                Text.of("Сумма (АР):")
        );
        this.addDrawableChild(amountField);

        // Поле для комментария
        this.commentField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2,
                200, 20,
                Text.of("Комментарий (не обязательно)")
        );
        this.addDrawableChild(commentField);

        // Кнопка перевода
        ButtonWidget transferButton = ButtonWidget.builder(Text.of("Перевести"), button -> {
                    processTransfer();
                })
                .dimensions(this.width / 2 - 100, this.height / 2 + 30, 200, 20)
                .build();
        this.addDrawableChild(transferButton);
        // Кнопка подтверждения (изначально скрыта)
    }

    private void processTransfer() {
        try {
            String receiverCard = receiverCardField.getText().trim();
            if (receiverCard.isEmpty()) {
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
            } catch (Exception ex) {
                setStatus("❌ Сумма не указана", 0xFF5555);
                return;
            }

            if (amount <= 0) {
                setStatus("❌ Сумма должна быть > 0", 0xFF5555);
                return;
            }

            // Получаем карту-отправителя из конфига
            String senderId = SPmHelperClient.config.getSpID();
            String senderToken = SPmHelperClient.config.getSpTOKEN();
            if (senderId == null || senderToken == null) {
                setStatus("❌ Привяжите карту (/spmhelper)", 0xFF5555);
                return;
            }
            Card senderCard = new Card(senderId, senderToken);
            if (SPWorldsApi.getBalance(senderCard) < amount) {
                setStatus("❌Слишком большая сумма для перевода", 0xFF5555);
                return;
            }

            // Создаем перевод: senderCard -> receiverCard
            JsonObject response = SPWorldsApi.createTransfer(
                    senderCard,
                    receiverCard,
                    amount,
                    commentField.getText()
            );
            if (response.has("error")) {
                String error = response.get("error").toString();
                if (error.contains("receiverIsSender")) {
                    setStatus("❌ Нельзя отправить деньги самому себе", 0xFF5555);
                } else if (error.contains("receiverCardNotFound")) {
                    setStatus("❌ Неправильно указан номер карты", 0xFF5555);
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
        String id = SPmHelperClient.config.getSpID();
        String token = SPmHelperClient.config.getSpTOKEN();

        if (id == null || token == null) {
            setStatus("❌ Карта не привязана", 0xFF5555);
            return;
        }

        Card card = new Card(id, token);
        JsonObject cardInfo = SPWorldsApi.getCardInfo(card);

        if (cardInfo.has("error")) {
            setStatus("❌ Ошибка загрузки карты", 0xFF5555);
        } else {
            setStatus("✔ Ваша карта привязана. Текущий баланс: " + cardInfo.get("balance").getAsString() + " АР", 0x55FF55);
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
                this.height / 2 - 80,
                0xA0A0A0,
                true
        );

        context.drawText(
                this.textRenderer,
                Text.of("Сумма (АР):"),
                this.width / 2 - 100,
                this.height / 2 - 45,
                0xA0A0A0,
                true
        );

        context.drawText(
                this.textRenderer,
                Text.of("Комментарий:"),
                this.width / 2 - 100,
                this.height / 2 - 10,
                0xA0A0A0,
                true
        );

        // Отображение статуса
        if (statusMessage != null) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.of(statusMessage),
                    this.width / 2,
                    this.height / 2 + 60,
                    statusColor
            );
        }

        Identifier CallsText = Identifier.of("spmhelper", "titles/paystextrender.png");
        int imageY = height / 2 - 180;
        int originalWidth = 674 / 2;
        int originalHeight = 123 / 2;
        int availableWidth = width - 40;
        int finalWidth = originalWidth;
        int finalHeight = originalHeight;
        if (originalWidth > availableWidth) {
            float scale = (float) availableWidth / originalWidth;
            finalWidth = availableWidth;
            finalHeight = (int) (originalHeight * scale);
        }
        int imageX = (width - finalWidth) / 2;
        context.drawTexture(CallsText, imageX, imageY, 0, 0, finalWidth, finalHeight, finalWidth, finalHeight);

    }
}