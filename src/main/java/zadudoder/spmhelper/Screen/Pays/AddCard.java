package zadudoder.spmhelper.Screen.Pays;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class AddCard extends Screen {

    public AddCard() {
        super(Text.of("Экран принятия карты"));
    }

    protected void init() {
        ButtonWidget AcceptButton = ButtonWidget.builder(Text.of("Принять"), (btn) -> {
                // Логика принятия, запись в конфиг
        }).dimensions(width/2-40, height/2, 80, 20).build();
        this.addDrawableChild(AcceptButton);

        ButtonWidget Dismiss = ButtonWidget.builder(Text.of("Отклонить"), (btn) -> {
            // Логика отклонения карты, наверное просто set screen close
        }).dimensions(width/2+40, height/2, 80, 20).build();
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

