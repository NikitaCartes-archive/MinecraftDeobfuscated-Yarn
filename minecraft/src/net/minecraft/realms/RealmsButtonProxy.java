package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4185;

@Environment(EnvType.CLIENT)
public class RealmsButtonProxy extends class_4185 implements RealmsAbstractButtonProxy<RealmsButton> {
	private final RealmsButton button;

	public RealmsButtonProxy(RealmsButton realmsButton, int i, int j, String string, int k, int l, class_4185.class_4241 arg) {
		super(i, j, k, l, string, arg);
		this.button = realmsButton;
	}

	@Override
	public boolean active() {
		return this.active;
	}

	@Override
	public void active(boolean bl) {
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

	public int method_2066() {
		return this.y;
	}

	@Override
	public void onClick(double d, double e) {
		this.button.onPress();
	}

	@Override
	public void onRelease(double d, double e) {
		this.button.onRelease(d, e);
	}

	@Override
	public void renderBg(class_310 arg, int i, int j) {
		this.button.renderBg(i, j);
	}

	public RealmsButton getButton() {
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
