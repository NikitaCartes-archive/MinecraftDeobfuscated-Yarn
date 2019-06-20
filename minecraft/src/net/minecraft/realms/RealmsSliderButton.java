package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_3532;

@Environment(EnvType.CLIENT)
public abstract class RealmsSliderButton extends AbstractRealmsButton<RealmsSliderButtonProxy> {
	protected static final class_2960 WIDGETS_LOCATION = new class_2960("textures/gui/widgets.png");
	private final int field_23665;
	private final RealmsSliderButtonProxy proxy;
	private final double minValue;
	private final double maxValue;

	public RealmsSliderButton(int i, int j, int k, int l, int m, double d, double e) {
		this.field_23665 = i;
		this.minValue = d;
		this.maxValue = e;
		this.proxy = new RealmsSliderButtonProxy(this, j, k, l, 20, this.toPct((double)m));
		this.getProxy().setMessage(this.getMessage());
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
		return class_3532.method_15350(d, this.minValue, this.maxValue);
	}

	public int getYImage(boolean bl) {
		return 0;
	}

	public void onClick(double d, double e) {
	}

	public void onRelease(double d, double e) {
	}

	public RealmsSliderButtonProxy getProxy() {
		return this.proxy;
	}

	public double getValue() {
		return this.proxy.getValue();
	}

	public void setValue(double d) {
		this.proxy.setValue(d);
	}

	public int method_26766() {
		return this.field_23665;
	}

	public void setMessage(String string) {
		this.proxy.setMessage(string);
	}

	public int getWidth() {
		return this.proxy.getWidth();
	}

	public int getHeight() {
		return this.proxy.getHeight();
	}

	public int method_26767() {
		return this.proxy.method_26768();
	}

	public abstract void applyValue();

	public void updateMessage() {
		this.proxy.setMessage(this.getMessage());
	}
}
