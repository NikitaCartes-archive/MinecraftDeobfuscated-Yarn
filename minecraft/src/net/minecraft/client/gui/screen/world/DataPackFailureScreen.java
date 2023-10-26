package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DataPackFailureScreen extends Screen {
	private MultilineText wrappedText = MultilineText.EMPTY;
	private final Runnable goBack;
	private final Runnable runServerInSafeMode;

	public DataPackFailureScreen(Runnable goBack, Runnable runServerInSafeMode) {
		super(Text.translatable("datapackFailure.title"));
		this.goBack = goBack;
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
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.goBack.run()).dimensions(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build()
		);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.wrappedText.drawCenterWithShadow(context, this.width / 2, 70);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
