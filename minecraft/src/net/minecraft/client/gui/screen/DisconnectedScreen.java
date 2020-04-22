package net.minecraft.client.gui.screen;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DisconnectedScreen extends Screen {
	private final Text reason;
	@Nullable
	private List<Text> reasonFormatted;
	private final Screen parent;
	private int reasonHeight;

	public DisconnectedScreen(Screen parent, String title, Text reason) {
		super(new TranslatableText(title));
		this.parent = parent;
		this.reason = reason;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.reasonFormatted = this.textRenderer.wrapLines(this.reason, this.width - 50);
		this.reasonHeight = this.reasonFormatted.size() * 9;
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				Math.min(this.height / 2 + this.reasonHeight / 2 + 9, this.height - 30),
				200,
				20,
				new TranslatableText("gui.toMenu"),
				buttonWidget -> this.client.openScreen(this.parent)
			)
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.method_27534(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.reasonHeight / 2 - 9 * 2, 11184810);
		int i = this.height / 2 - this.reasonHeight / 2;
		if (this.reasonFormatted != null) {
			for (Text text : this.reasonFormatted) {
				this.method_27534(matrices, this.textRenderer, text, this.width / 2, i, 16777215);
				i += 9;
			}
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
