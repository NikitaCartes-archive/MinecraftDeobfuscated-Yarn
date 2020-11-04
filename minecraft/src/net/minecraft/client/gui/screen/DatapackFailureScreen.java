package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class DatapackFailureScreen extends Screen {
	private class_5489 wrappedText = class_5489.field_26528;
	private final Runnable field_25452;

	public DatapackFailureScreen(Runnable runnable) {
		super(new TranslatableText("datapackFailure.title"));
		this.field_25452 = runnable;
	}

	@Override
	protected void init() {
		super.init();
		this.wrappedText = class_5489.method_30890(this.textRenderer, this.getTitle(), this.width - 50);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableText("datapackFailure.safeMode"), buttonWidget -> this.field_25452.run()
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableText("gui.toTitle"), buttonWidget -> this.client.openScreen(null)
			)
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.wrappedText.method_30888(matrices, this.width / 2, 70);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
