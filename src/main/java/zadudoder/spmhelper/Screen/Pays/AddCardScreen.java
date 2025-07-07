package zadudoder.spmhelper.Screen.Pays;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;

public class AddCardScreen extends Screen {
    private final String id;
    private final String token;
    private final String name;

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

        ButtonWidget AcceptButton = ButtonWidget.builder(Text.translatable("text.spmhelper.addCard_AcceptButton"), (btn) -> {
            if (id == null) {
                this.client.player.sendMessage(Text.translatable("text.spmhelper.status_FeedBackMessageCaseDefault"));
            } else {
                SPmHelperConfig.get().addCard(id, token, name); //Добавление карты
                this.client.player.sendMessage(Text.translatable("text.spmhelper.addCard_AcceptFeedBack"));
            }
            this.close();
        }).dimensions(width / 2 - 120, height / 2, 80, 20).build();
        this.addDrawableChild(AcceptButton);

        ButtonWidget DeclineButton = ButtonWidget.builder(Text.translatable("text.spmhelper.addCard_DeclineButton"), (btn) -> {
            this.close();
        }).dimensions(width / 2 + 40, height / 2, 80, 20).build();
        this.addDrawableChild(DeclineButton);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.spmhelper.addCard_DoYouWantAdd", name),
                this.width / 2,
                this.height / 2 - 40,
                0xFFFFFF
        );

    }
}

