package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_357 extends class_339 {
	private double field_2161 = 1.0;
	public boolean field_2163;
	private final class_315.class_316 field_2162;
	private final double field_2160;
	private final double field_2159;

	public class_357(int i, int j, int k, class_315.class_316 arg) {
		this(i, j, k, arg, 0.0, 1.0);
	}

	public class_357(int i, int j, int k, class_315.class_316 arg, double d, double e) {
		this(i, j, k, 150, 20, arg, d, e);
	}

	public class_357(int i, int j, int k, int l, int m, class_315.class_316 arg, double d, double e) {
		super(i, j, k, l, m, "");
		this.field_2162 = arg;
		this.field_2160 = d;
		this.field_2159 = e;
		class_310 lv = class_310.method_1551();
		this.field_2161 = arg.method_1651(lv.field_1690.method_1637(arg));
		this.field_2074 = lv.field_1690.method_1642(arg);
	}

	@Override
	protected int method_1830(boolean bl) {
		return 0;
	}

	@Override
	protected void method_1827(class_310 arg, int i, int j) {
		if (this.field_2076) {
			if (this.field_2163) {
				this.field_2161 = (double)((float)(i - (this.field_2069 + 4)) / (float)(this.field_2071 - 8));
				this.field_2161 = class_3532.method_15350(this.field_2161, 0.0, 1.0);
			}

			if (this.field_2163 || this.field_2162 == class_315.class_316.field_1931) {
				double d = this.field_2162.method_1645(this.field_2161);
				arg.field_1690.method_1625(this.field_2162, d);
				this.field_2161 = this.field_2162.method_1651(d);
				this.field_2074 = arg.field_1690.method_1642(this.field_2162);
			}

			arg.method_1531().method_4618(field_2072);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_1788(this.field_2069 + (int)(this.field_2161 * (double)(this.field_2071 - 8)), this.field_2068, 0, 66, 4, 20);
			this.method_1788(this.field_2069 + (int)(this.field_2161 * (double)(this.field_2071 - 8)) + 4, this.field_2068, 196, 66, 4, 20);
		}
	}

	@Override
	public final void method_1826(double d, double e) {
		this.field_2161 = (d - (double)(this.field_2069 + 4)) / (double)(this.field_2071 - 8);
		this.field_2161 = class_3532.method_15350(this.field_2161, 0.0, 1.0);
		class_310 lv = class_310.method_1551();
		lv.field_1690.method_1625(this.field_2162, this.field_2162.method_1645(this.field_2161));
		this.field_2074 = lv.field_1690.method_1642(this.field_2162);
		this.field_2163 = true;
	}

	@Override
	public void method_1831(double d, double e) {
		this.field_2163 = false;
	}
}
