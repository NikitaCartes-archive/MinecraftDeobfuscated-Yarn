package net.minecraft.realms;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_3532;

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
		this.getProxy().field_2074 = this.getMessage();
	}

	public String getMessage() {
		return "";
	}

	public double toPct(double d) {
		return class_3532.method_15350((this.clamp(d) - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
	}

	public double toValue(double d) {
		return this.clamp(class_3532.method_16436(class_3532.method_15350(d, 0.0, 1.0), this.minValue, this.maxValue));
	}

	public double clamp(double d) {
		d = this.clampSteps(d);
		return class_3532.method_15350(d, this.minValue, this.maxValue);
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
		if (this.getProxy().field_2076) {
			if (this.sliding) {
				this.value = (double)((float)(i - (this.getProxy().field_2069 + 4)) / (float)(this.getProxy().method_1825() - 8));
				this.value = class_3532.method_15350(this.value, 0.0, 1.0);
				double d = this.toValue(this.value);
				this.clicked(d);
				this.value = this.toPct(d);
				this.getProxy().field_2074 = this.getMessage();
			}

			class_310.method_1551().method_1531().method_4618(WIDGETS_LOCATION);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.blit(this.getProxy().field_2069 + (int)(this.value * (double)(this.getProxy().method_1825() - 8)), this.getProxy().field_2068, 0, 66, 4, 20);
			this.blit(this.getProxy().field_2069 + (int)(this.value * (double)(this.getProxy().method_1825() - 8)) + 4, this.getProxy().field_2068, 196, 66, 4, 20);
		}
	}

	@Override
	public void onClick(double d, double e) {
		this.value = (d - (double)(this.getProxy().field_2069 + 4)) / (double)(this.getProxy().method_1825() - 8);
		this.value = class_3532.method_15350(this.value, 0.0, 1.0);
		this.clicked(this.toValue(this.value));
		this.getProxy().field_2074 = this.getMessage();
		this.sliding = true;
	}

	public void clicked(double d) {
	}

	@Override
	public void onRelease(double d, double e) {
		this.sliding = false;
	}
}
