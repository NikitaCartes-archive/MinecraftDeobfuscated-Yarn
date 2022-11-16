package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DatapackFailureScreen extends Screen {
	private MultilineText wrappedText = MultilineText.EMPTY;
	private final Runnable runServerInSafeMode;

	public DatapackFailureScreen(Runnable runServerInSafeMode) {
		super(Text.translatable("datapackFailure.title"));
		this.runServerInSafeMode = runServerInSafeMode;
	}

	@Override
	protected void init() {
		super.init();
		this.wrappedText = MultilineText.create(this.textRenderer, this.getTitle(), this.width - 50);
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("datapackFailure.safeMode"), button -> this.runServerInSafeMode.run())
				.dimensions(this.width / 2 - 155, this.height / 6 + 96, 150, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("gui.toTitle"), button -> this.client.setScreen(null))
				.dimensions(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20)
				.build()
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.wrappedText.drawCenterWithShadow(matrices, this.width / 2, 70);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
