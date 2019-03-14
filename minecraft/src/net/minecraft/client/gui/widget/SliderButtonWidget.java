package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.RealmsButton;
import net.minecraft.realms.RealmsSliderButton;

@Environment(EnvType.CLIENT)
public class SliderButtonWidget extends SliderWidget implements RealmsButton<RealmsSliderButton> {
	private final RealmsSliderButton field_18729;

	public SliderButtonWidget(RealmsSliderButton realmsSliderButton, int i, int j, int k, int l, double d) {
		super(i, j, k, l, d);
		this.field_18729 = realmsSliderButton;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean bl) {
		this.enabled = bl;
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
	public void setText(String string) {
		super.setText(string);
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	public int getY() {
		return this.y;
	}

	@Override
	public void method_19347(double d, double e) {
		this.field_18729.onClick(d, e);
	}

	@Override
	public void onReleased(double d, double e) {
		this.field_18729.onRelease(d, e);
	}

	@Override
	protected void updateText() {
		this.field_18729.updateMessage();
	}

	@Override
	protected void onProgressChanged() {
		this.field_18729.applyValue();
	}

	public double getValue() {
		return this.progress;
	}

	public void setValue(double d) {
		this.progress = d;
	}

	@Override
	public void drawBackground(MinecraftClient minecraftClient, int i, int j) {
		super.drawBackground(minecraftClient, i, j);
	}

	public RealmsSliderButton method_19364() {
		return this.field_18729;
	}

	@Override
	public int getTextureId(boolean bl) {
		return this.field_18729.getYImage(bl);
	}

	public int getHeight() {
		return this.height;
	}
}
