package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.RealmsButton;
import net.minecraft.realms.RealmsSliderButton;

@Environment(EnvType.CLIENT)
public class SliderButtonWidget extends SliderWidget implements RealmsButton<RealmsSliderButton> {
	private final RealmsSliderButton button;

	public SliderButtonWidget(RealmsSliderButton realmsSliderButton, int i, int j, int k, int l, double d) {
		super(i, j, k, l, d);
		this.button = realmsSliderButton;
	}

	@Override
	public boolean isEnabled() {
		return this.active;
	}

	@Override
	public void setEnabled(boolean bl) {
		this.active = bl;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(boolean bl) {
		this.visible = bl;
	}

	@Override
	public void setMessage(String string) {
		super.setMessage(string);
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	public int getY() {
		return this.y;
	}

	@Override
	public void onClick(double d, double e) {
		this.button.onClick(d, e);
	}

	@Override
	public void onRelease(double d, double e) {
		this.button.onRelease(d, e);
	}

	@Override
	public void updateText() {
		this.button.updateMessage();
	}

	@Override
	public void onProgressChanged() {
		this.button.applyValue();
	}

	public double getValue() {
		return this.progress;
	}

	public void setValue(double d) {
		this.progress = d;
	}

	@Override
	public void renderBg(MinecraftClient minecraftClient, int i, int j) {
		super.renderBg(minecraftClient, i, j);
	}

	public RealmsSliderButton getButton() {
		return this.button;
	}

	@Override
	public int getYImage(boolean bl) {
		return this.button.getYImage(bl);
	}

	public int getSuperYImage(boolean bl) {
		return super.getYImage(bl);
	}

	public int getHeight() {
		return this.height;
	}
}
