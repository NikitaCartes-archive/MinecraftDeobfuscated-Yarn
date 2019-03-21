package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.RealmsButtonWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class RealmsButton extends RealmsAbstractButton<RealmsButtonWidget> {
	protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	private final int field_18237;
	private final RealmsButtonWidget proxy;

	public RealmsButton(int i, int j, int k, String string) {
		this(i, j, k, 200, 20, string);
	}

	public RealmsButton(int i, int j, int k, int l, int m, String string) {
		this.field_18237 = i;
		this.proxy = new RealmsButtonWidget(this, j, k, string, l, m, buttonWidget -> this.onPress());
	}

	public RealmsButtonWidget getProxy() {
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
		return this.proxy.getY();
	}

	public int method_19461() {
		return this.proxy.getHeight();
	}

	public void renderBg(int i, int j) {
	}

	public int getYImage(boolean bl) {
		return this.proxy.getTexId(bl);
	}

	public abstract void onPress();

	public void onRelease(double d, double e) {
	}
}
