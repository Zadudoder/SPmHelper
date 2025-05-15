package zadudoder.spmhelper.Screen.Laws;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LawsScreen extends Screen {
    public LawsScreen() {
        super(Text.of("Экран законы"));
    }

    protected void init() {

    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Канал с Законами, на данном этапе находится в разработке"),
                this.width / 2,
                this.height / 2,
                0xFFFFFF
        );
    }
}

