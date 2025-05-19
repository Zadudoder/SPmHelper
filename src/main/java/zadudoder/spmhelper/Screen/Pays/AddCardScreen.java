package zadudoder.spmhelper.Screen.Pays;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import zadudoder.spmhelper.SPmHelperClient;

public class AddCardScreen extends Screen {
    private String id;
    private String token;
    private String name;

    public AddCardScreen() {
        super(Text.of("Экран принятия карты"));
    }

    public AddCardScreen(String id, String token, String name) {
        super(Text.of("Экран принятия карты"));
        this.id = id;
        this.token = token;
        this.name = name;

    }

    protected void init() {
        ButtonWidget AcceptButton = ButtonWidget.builder(Text.of("Принять"), (btn) -> {
            SPmHelperClient.config.addCard(id, token, name); //Добавление карты
            this.close();
            this.client.player.sendMessage(Text.literal("§a[SPmHelper]: Карта успешно привязана!"));

        }).dimensions(width / 2 - 40, height / 2, 80, 20).build();
        this.addDrawableChild(AcceptButton);

        ButtonWidget Dismiss = ButtonWidget.builder(Text.of("Отклонить"), (btn) -> {
            this.close();
        }).dimensions(width / 2 + 40, height / 2, 80, 20).build();
        this.addDrawableChild(Dismiss);


    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Вы хотите добавить карту " + "?"),
                this.width / 2,
                this.height / 2 - 40,
                0xFFFFFF
        );

    }
}

