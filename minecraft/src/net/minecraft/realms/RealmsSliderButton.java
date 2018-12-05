package net.minecraft.realms;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class RealmsSliderButton extends RealmsButton {
	public double value = 1.0;
	public boolean sliding;
	private final double minValue;
	private final double maxValue;
	private int steps;

	public RealmsSliderButton(int i, int j, int k, int l, int m, int n) {
		this(i, j, k, l, n, 0, 1.0, (double)m);
	}

	public RealmsSliderButton(int i, int j, int k, int l, int m, int n, double d, double e) {
		super(i, j, k, l, 20, "");
		this.minValue = d;
		this.maxValue = e;
		this.value = this.toPct((double)n);
		this.getProxy().text = this.getMessage();
	}

	public String getMessage() {
		return "";
	}

	public double toPct(double d) {
		return MathHelper.clamp((this.clamp(d) - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
	}

	public double toValue(double d) {
		return this.clamp(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.minValue, this.maxValue));
	}

	public double clamp(double d) {
		d = this.clampSteps(d);
		return MathHelper.clamp(d, this.minValue, this.maxValue);
	}

	protected double clampSteps(double d) {
		if (this.steps > 0) {
			d = (double)((long)this.steps * Math.round(d / (double)this.steps));
		}

		return d;
	}

	@Override
	public int getYImage(boolean bl) {
		return 0;
	}

	@Override
	public void renderBg(int i, int j) {
		if (this.getProxy().visible) {
			if (this.sliding) {
				this.value = (double)((float)(i - (this.getProxy().x + 4)) / (float)(this.getProxy().getWidth() - 8));
				this.value = MathHelper.clamp(this.value, 0.0, 1.0);
				double d = this.toValue(this.value);
				this.clicked(d);
				this.value = this.toPct(d);
				this.getProxy().text = this.getMessage();
			}

			MinecraftClient.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.blit(this.getProxy().x + (int)(this.value * (double)(this.getProxy().getWidth() - 8)), this.getProxy().y, 0, 66, 4, 20);
			this.blit(this.getProxy().x + (int)(this.value * (double)(this.getProxy().getWidth() - 8)) + 4, this.getProxy().y, 196, 66, 4, 20);
		}
	}

	@Override
	public void onClick(double d, double e) {
		this.value = (d - (double)(this.getProxy().x + 4)) / (double)(this.getProxy().getWidth() - 8);
		this.value = MathHelper.clamp(this.value, 0.0, 1.0);
		this.clicked(this.toValue(this.value));
		this.getProxy().text = this.getMessage();
		this.sliding = true;
	}

	public void clicked(double d) {
	}

	@Override
	public void onRelease(double d, double e) {
		this.sliding = false;
	}
}
