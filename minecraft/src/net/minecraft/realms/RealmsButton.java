package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_339;
import net.minecraft.class_398;

@Environment(EnvType.CLIENT)
public abstract class RealmsButton {
	protected static final class_2960 WIDGETS_LOCATION = new class_2960("textures/gui/widgets.png");
	private final class_398 proxy;

	public RealmsButton(int i, int j, int k, String string) {
		this.proxy = new class_398(this, i, j, k, string) {
			@Override
			public void method_1826(double d, double e) {
				RealmsButton.this.onClick(d, e);
			}
		};
	}

	public RealmsButton(int i, int j, int k, int l, int m, String string) {
		this.proxy = new class_398(this, i, j, k, string, l, m) {
			@Override
			public void method_1826(double d, double e) {
				RealmsButton.this.onClick(d, e);
			}
		};
	}

	public class_339 getProxy() {
		return this.proxy;
	}

	public int method_10253() {
		return this.proxy.method_2063();
	}

	public boolean active() {
		return this.proxy.method_2067();
	}

	public void active(boolean bl) {
		this.proxy.method_2062(bl);
	}

	public void msg(String string) {
		this.proxy.method_2060(string);
	}

	public int getWidth() {
		return this.proxy.method_1825();
	}

	public int getHeight() {
		return this.proxy.method_2066();
	}

	public int method_10254() {
		return this.proxy.method_2065();
	}

	public void render(int i, int j, float f) {
		this.proxy.method_1824(i, j, f);
	}

	public void blit(int i, int j, int k, int l, int m, int n) {
		this.proxy.method_1788(i, j, k, l, m, n);
	}

	public void renderBg(int i, int j) {
	}

	public int getYImage(boolean bl) {
		return this.proxy.method_2061(bl);
	}

	public abstract void onClick(double d, double e);

	public void onRelease(double d, double e) {
	}
}
