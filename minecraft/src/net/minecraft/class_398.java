package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsButton;

@Environment(EnvType.CLIENT)
public class class_398 extends class_339 {
	private final RealmsButton field_2339;

	public class_398(RealmsButton realmsButton, int i, int j, int k, String string) {
		super(i, j, k, string);
		this.field_2339 = realmsButton;
	}

	public class_398(RealmsButton realmsButton, int i, int j, int k, String string, int l, int m) {
		super(i, j, k, l, m, string);
		this.field_2339 = realmsButton;
	}

	public int method_2063() {
		return this.field_2077;
	}

	public boolean method_2067() {
		return this.field_2078;
	}

	public void method_2062(boolean bl) {
		this.field_2078 = bl;
	}

	public void method_2060(String string) {
		super.field_2074 = string;
	}

	@Override
	public int method_1825() {
		return super.method_1825();
	}

	public int method_2065() {
		return this.field_2068;
	}

	@Override
	public void method_1826(double d, double e) {
		this.field_2339.onClick(d, e);
	}

	@Override
	public void method_1831(double d, double e) {
		this.field_2339.onRelease(d, e);
	}

	@Override
	public void method_1827(class_310 arg, int i, int j) {
		this.field_2339.renderBg(i, j);
	}

	public RealmsButton method_2064() {
		return this.field_2339;
	}

	@Override
	public int method_1830(boolean bl) {
		return this.field_2339.getYImage(bl);
	}

	public int method_2061(boolean bl) {
		return super.method_1830(bl);
	}

	public int method_2066() {
		return this.field_2070;
	}
}
