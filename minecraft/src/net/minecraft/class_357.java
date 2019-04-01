package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_357 extends class_339 {
	protected final class_315 options;
	protected double value;

	protected class_357(int i, int j, int k, int l, double d) {
		this(class_310.method_1551().field_1690, i, j, k, l, d);
	}

	protected class_357(class_315 arg, int i, int j, int k, int l, double d) {
		super(i, j, k, l, "");
		this.options = arg;
		this.value = d;
	}

	@Override
	protected int getYImage(boolean bl) {
		return 0;
	}

	@Override
	protected String getNarrationMessage() {
		return class_1074.method_4662("gui.narrate.slider", this.getMessage());
	}

	@Override
	protected void renderBg(class_310 arg, int i, int j) {
		arg.method_1531().method_4618(WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = (this.isHovered() ? 2 : 1) * 20;
		this.blit(this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + k, 4, 20);
		this.blit(this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + k, 4, 20);
	}

	@Override
	public void onClick(double d, double e) {
		this.setValueFromMouse(d);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 263) {
			double d = this.value;
			double e = 1.0 / (double)(this.width - 8);
			this.value = class_3532.method_15350(this.value - e, 0.0, 1.0);
			if (d != this.value) {
				this.applyValue();
			}

			this.updateMessage();
		} else if (i == 262) {
			double d = this.value;
			double e = 1.0 / (double)(this.width - 8);
			this.value = class_3532.method_15350(this.value + e, 0.0, 1.0);
			if (d != this.value) {
				this.applyValue();
			}

			this.updateMessage();
		}

		return false;
	}

	private void setValueFromMouse(double d) {
		double e = this.value;
		this.value = class_3532.method_15350((d - (double)(this.x + 4)) / (double)(this.width - 8), 0.0, 1.0);
		if (e != this.value) {
			this.applyValue();
		}

		this.updateMessage();
	}

	@Override
	protected void onDrag(double d, double e, double f, double g) {
		this.setValueFromMouse(d);
		super.onDrag(d, e, f, g);
	}

	@Override
	public void playDownSound(class_1144 arg) {
	}

	@Override
	public void onRelease(double d, double e) {
		super.playDownSound(class_310.method_1551().method_1483());
	}

	protected abstract void updateMessage();

	protected abstract void applyValue();
}
