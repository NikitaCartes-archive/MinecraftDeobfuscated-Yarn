package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class OutOfMemoryScreen extends Screen {
	public OutOfMemoryScreen() {
		super(new LiteralText("Out of memory!"));
	}

	@Override
	protected void init() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, new TranslatableText("gui.toTitle"), buttonWidget -> this.client.openScreen(new TitleScreen())
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, new TranslatableText("menu.quit"), buttonWidget -> this.client.scheduleStop()
			)
		);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.method_27534(matrices, this.textRenderer, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
		this.drawString(matrices, this.textRenderer, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
		this.drawString(
			matrices, this.textRenderer, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880
		);
		this.drawString(
			matrices, this.textRenderer, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880
		);
		this.drawString(matrices, this.textRenderer, "memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
		this.drawString(
			matrices, this.textRenderer, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 54, 10526880
		);
		this.drawString(
			matrices, this.textRenderer, "We've tried to free up enough memory to let you go back to", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880
		);
		this.drawString(
			matrices,
			this.textRenderer,
			"the main menu and back to playing, but this may not have worked.",
			this.width / 2 - 140,
			this.height / 4 - 60 + 60 + 72,
			10526880
		);
		this.drawString(
			matrices, this.textRenderer, "Please restart the game if you see this message again.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880
		);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
