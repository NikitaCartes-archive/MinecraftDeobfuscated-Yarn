package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class FatalErrorScreen extends Screen {
	private final Text message;

	public FatalErrorScreen(Text title, Text message) {
		super(title);
		this.message = message;
	}

	@Override
	protected void init() {
		super.init();
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(null)).dimensions(this.width / 2 - 100, 140, 200, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 90, 16777215);
		context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, 110, 16777215);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		context.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
