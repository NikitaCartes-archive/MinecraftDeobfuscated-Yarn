package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;

@Environment(EnvType.CLIENT)
public abstract class RealmsButton extends AbstractRealmsButton<RealmsButtonProxy> {
	protected static final class_2960 WIDGETS_LOCATION = new class_2960("textures/gui/widgets.png");
	private final int field_18237;
	private final RealmsButtonProxy proxy;

	public RealmsButton(int i, int j, int k, String string) {
		this(i, j, k, 200, 20, string);
	}

	public RealmsButton(int i, int j, int k, int l, int m, String string) {
		this.field_18237 = i;
		this.proxy = new RealmsButtonProxy(this, j, k, string, l, m, arg -> this.onPress());
	}

	public RealmsButtonProxy getProxy() {
		return this.proxy;
	}

	public int method_10253() {
		return this.field_18237;
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

	public int method_19461() {
		return this.proxy.method_2066();
	}

	public void renderBg(int i, int j) {
	}

	public int getYImage(boolean bl) {
		return this.proxy.getSuperYImage(bl);
	}

	public abstract void onPress();

	public void onRelease(double d, double e) {
	}
}
