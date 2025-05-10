package zadudoder.spmhelper.Screen.Map;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MapScreen extends Screen {

    public MapScreen() {
        super(Text.of("Экран карты"));
    }

    protected void init() {

    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
