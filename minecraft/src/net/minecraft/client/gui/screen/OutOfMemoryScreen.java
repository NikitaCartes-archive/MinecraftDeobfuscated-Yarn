package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class OutOfMemoryScreen extends Screen {
	private MultilineText message = MultilineText.EMPTY;

	public OutOfMemoryScreen() {
		super(new TranslatableText("outOfMemory.error"));
	}

	@Override
	protected void init() {
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, new TranslatableText("gui.toTitle"), button -> this.client.setScreen(new TitleScreen())
			)
		);
		this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, new TranslatableText("menu.quit"), button -> this.client.scheduleStop())
		);
		this.message = MultilineText.create(this.textRenderer, new TranslatableText("outOfMemory.message"), 295);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
		this.message.drawWithShadow(matrices, this.width / 2 - 145, this.height / 4, 9, 10526880);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
