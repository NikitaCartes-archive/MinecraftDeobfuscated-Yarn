package net.minecraft.client.gui.screen;

import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * A screen that can be displayed while running a certain task. The task can be
 * cancelled, and the display can be changed by calling {@code setDisplay} methods after the
 * task succeeded or failed.
 */
@Environment(EnvType.CLIENT)
public class RunningTaskScreen extends Screen {
	private static final int TITLE_TEXT_Y = 80;
	private static final int DESCRIPTION_TEXT_Y = 120;
	private static final int DESCRIPTION_TEXT_WIDTH = 360;
	private final Text cancelButtonText;
	private Runnable buttonCallback;
	@Nullable
	private MultilineText description;
	private ButtonWidget button;
	private long buttonActivationTime;

	public RunningTaskScreen(Text title, Text cancelButtonText, Runnable cancelCallback) {
		super(title);
		this.cancelButtonText = cancelButtonText;
		this.buttonCallback = cancelCallback;
	}

	@Override
	protected void init() {
		super.init();
		this.replaceButton(this.cancelButtonText);
	}

	@Override
	public void tick() {
		this.button.active = Util.getMeasuringTimeMs() > this.buttonActivationTime;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 80, 16777215);
		if (this.description == null) {
			String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
			drawCenteredText(matrices, this.textRenderer, string, this.width / 2, 120, 10526880);
		} else {
			this.description.drawCenterWithShadow(matrices, this.width / 2, 120);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	/**
	 * Removes the description and changes the button text and callback to the provided one.
	 * The button will be deactivated for 1 second after calling this.
	 */
	public void setDisplay(Text buttonText, Runnable buttonCallback) {
		this.setDisplay(null, buttonText, buttonCallback);
	}

	/**
	 * Changes the description, the button text and callback to the provided one.
	 * The new description will be narrated, and the button will be deactivated for
	 * 1 second after calling this.
	 */
	public void setDisplay(@Nullable Text description, Text buttonText, Runnable buttonCallback) {
		this.buttonCallback = buttonCallback;
		if (description != null) {
			this.description = MultilineText.create(this.textRenderer, description, 360);
			NarratorManager.INSTANCE.narrate(description);
		} else {
			this.description = null;
		}

		this.replaceButton(buttonText);
		this.buttonActivationTime = Util.getMeasuringTimeMs() + TimeUnit.SECONDS.toMillis(1L);
	}

	private void replaceButton(Text buttonText) {
		this.remove(this.button);
		int i = 150;
		int j = 20;
		int k = this.description != null ? this.description.count() : 1;
		int l = Math.min(120 + (k + 4) * 9, this.height - 40);
		this.button = this.addDrawableChild(new ButtonWidget((this.width - 150) / 2, l, 150, 20, buttonText, button -> this.buttonCallback.run()));
	}
}
