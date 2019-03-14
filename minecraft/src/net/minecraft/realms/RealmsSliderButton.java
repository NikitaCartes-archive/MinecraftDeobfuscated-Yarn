package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.SliderButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class RealmsSliderButton extends RealmsAbstractButton<SliderButtonWidget> {
	protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	private final int field_18804;
	private final SliderButtonWidget proxy;
	private final double minValue;
	private final double maxValue;

	public RealmsSliderButton(int i, int j, int k, int l, int m, double d, double e) {
		this.field_18804 = i;
		this.minValue = d;
		this.maxValue = e;
		this.proxy = new SliderButtonWidget(this, j, k, l, 20, this.toPct((double)m));
		this.getProxy().setText(this.getMessage());
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
		return MathHelper.clamp(d, this.minValue, this.maxValue);
	}

	public int getYImage(boolean bl) {
		return 0;
	}

	public void onClick(double d, double e) {
	}

	public void onRelease(double d, double e) {
	}

	public SliderButtonWidget getProxy() {
		return this.proxy;
	}

	public double getValue() {
		return this.proxy.getValue();
	}

	public void setValue(double d) {
		this.proxy.setValue(d);
	}

	public int method_19462() {
		return this.field_18804;
	}

	public void setMessage(String string) {
		this.proxy.setText(string);
	}

	public int getWidth() {
		return this.proxy.getWidth();
	}

	public int getHeight() {
		return this.proxy.getHeight();
	}

	public int method_19463() {
		return this.proxy.getY();
	}

	public abstract void applyValue();

	public void updateMessage() {
		this.proxy.setText(this.getMessage());
	}
}
