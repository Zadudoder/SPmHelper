package zadudoder.spmhelper.Screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.net.URI;

public class QRcodeAcceptScreen extends Screen {
        private final String url;
        private final Screen parent;

        public QRcodeAcceptScreen(String url, Screen parent) {
            super(Text.of("Подтверждение перехода"));
            this.url = url;
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.spmhelper.cancel"), (button) -> {
                this.client.setScreen(parent);
            }).dimensions(this.width / 2 - 155, this.height / 2 + 30, 150, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.spmhelper.followTheLink"), (button) -> {
                try {
                    Util.getOperatingSystem().open(new URI(url));
                } catch (Exception e) {
                    this.client.player.sendMessage(Text.translatable("text.spmhelper.FailedOpenLink"), false);
                }
                this.client.setScreen(parent);
            }).dimensions(this.width / 2 + 5, this.height / 2 + 30, 150, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.spmhelper.AcceptLinkClick"),
                    this.width / 2,
                    this.height / 2 - 30,
                    0xFFFFFF
            );
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.of("§e" + url),
                    this.width / 2,
                    this.height / 2 - 10,
                    300
            );
        }
    }
