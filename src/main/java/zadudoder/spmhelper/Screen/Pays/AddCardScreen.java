package zadudoder.spmhelper.Screen.Pays;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;

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
        ButtonWidget SPmGroup = ButtonWidget.builder(Text.of("✈"), (btn) -> {
            Util.getOperatingSystem().open("https://spmhelper.ru");
        }).dimensions(width - 20, 10, 15, 15).build();
        this.addDrawableChild(SPmGroup);

        ButtonWidget Back = ButtonWidget.builder(Text.of("⬅"), (btn) -> {
            this.client.setScreen(new MainScreen());
        }).dimensions(5, 10, 15, 15).build();
        this.addDrawableChild(Back);

        ButtonWidget AcceptButton = ButtonWidget.builder(Text.of("Принять"), (btn) -> {
            SPmHelperConfig.get().addCard(id, token, name); //Добавление карты
            this.close();
            this.client.player.sendMessage(Text.literal("§a[SPmHelper]: Карта успешно привязана!"));
        }).dimensions(width / 2 - 120, height / 2, 80, 20).build();
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
                Text.of("Вы хотите добавить карту " + name + " ?"),
                this.width / 2,
                this.height / 2 - 40,
                0xFFFFFF
        );

    }
}

