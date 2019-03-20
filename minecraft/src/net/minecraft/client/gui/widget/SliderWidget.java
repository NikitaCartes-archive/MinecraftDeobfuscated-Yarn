package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class SliderWidget extends AbstractButtonWidget {
	protected final GameOptions gameOptions;
	protected double progress;

	protected SliderWidget(int i, int j, int k, int l, double d) {
		this(MinecraftClient.getInstance().options, i, j, k, l, d);
	}

	protected SliderWidget(GameOptions gameOptions, int i, int j, int k, int l, double d) {
		super(i, j, k, l, "");
		this.gameOptions = gameOptions;
		this.progress = d;
	}

	@Override
	protected int getYImage(boolean bl) {
		return 0;
	}

	@Override
	protected String getNarrationMessage() {
		return I18n.translate("gui.narrate.slider", this.getMessage());
	}

	@Override
	protected void renderBg(MinecraftClient minecraftClient, int i, int j) {
		minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedRect(this.x + (int)(this.progress * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
		this.drawTexturedRect(this.x + (int)(this.progress * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			boolean bl = this.clicked(d, e);
			if (bl) {
				this.playDownSound(MinecraftClient.getInstance().getSoundManager());
				this.changeProgress(d);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 263) {
			double d = this.progress;
			double e = 1.0 / (double)(this.width - 8);
			this.progress = MathHelper.clamp(this.progress - e, 0.0, 1.0);
			if (d != this.progress) {
				this.onProgressChanged();
			}

			this.updateText();
		} else if (i == 262) {
			double d = this.progress;
			double e = 1.0 / (double)(this.width - 8);
			this.progress = MathHelper.clamp(this.progress + e, 0.0, 1.0);
			if (d != this.progress) {
				this.onProgressChanged();
			}

			this.updateText();
		}

		return false;
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
	protected void onDrag(double d, double e, double f, double g) {
		this.changeProgress(d);
		super.onDrag(d, e, f, g);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}

	@Override
	public void onRelease(double d, double e) {
		super.playDownSound(MinecraftClient.getInstance().getSoundManager());
	}

	protected abstract void updateText();

	protected abstract void onProgressChanged();
}
