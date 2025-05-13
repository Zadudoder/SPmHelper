package zadudoder.spmhelper.Screen.Pays;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MyCardScreen extends Screen {

    public MyCardScreen() {
        super(Text.of("Экран карточек"));
    }

    protected void init() {

    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Канал с Настройками, временно в разработке"),
                this.width / 2,
                this.height / 2,
                0xFFFFFF
        );
    }
}
