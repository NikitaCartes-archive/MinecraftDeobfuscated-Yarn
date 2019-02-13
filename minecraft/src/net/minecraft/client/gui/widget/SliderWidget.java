package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class SliderWidget extends ButtonWidget {
	protected final MinecraftClient client;
	protected double progress;

	protected SliderWidget(int i, int j, int k, int l, int m, MinecraftClient minecraftClient, float f) {
		super(i, j, k, l, m, "");
		this.client = minecraftClient;
		this.progress = (double)f;
	}

	@Override
	protected int getTextureId(boolean bl) {
		return 0;
	}

	@Override
	protected String getNarrationString() {
		return I18n.translate("gui.narrate.slider", this.getText());
	}

	@Override
	protected void drawBackground(MinecraftClient minecraftClient, int i, int j) {
		minecraftClient.getTextureManager().bindTexture(WIDGET_TEX);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedRect(this.x + (int)(this.progress * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
		this.drawTexturedRect(this.x + (int)(this.progress * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
	}

	@Override
	public void onPressed(double d, double e) {
		this.changeProgress(d);
	}

	private void changeProgress(double d) {
		double e = this.progress;
		this.progress = MathHelper.clamp((d - (double)(this.x + 4)) / (double)(this.width - 8), 0.0, 1.0);
		if (e != this.progress) {
			this.onProgressChanged();
		}

		this.updateText();
	}

	@Override
	protected void onDragged(double d, double e, double f, double g) {
		this.changeProgress(d);
		super.onDragged(d, e, f, g);
	}

	@Override
	public void playPressedSound(SoundLoader soundLoader) {
	}

	@Override
	public void onReleased(double d, double e) {
		super.playPressedSound(this.client.getSoundLoader());
	}

	protected abstract void updateText();

	protected abstract void onProgressChanged();
}
