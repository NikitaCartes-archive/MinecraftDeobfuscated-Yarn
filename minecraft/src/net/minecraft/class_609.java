package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_609<T extends class_1297> extends class_583<T> {
	private final class_630 field_3571;
	private final class_630 field_3573;
	private final class_630 field_3572;
	private final class_630 field_3570;

	public class_609(int i) {
		if (i > 0) {
			this.field_3571 = new class_630(this, 0, i);
			this.field_3571.method_2844(-3.0F, 17.0F, -3.0F, 6, 6, 6);
			this.field_3573 = new class_630(this, 32, 0);
			this.field_3573.method_2844(-3.25F, 18.0F, -3.5F, 2, 2, 2);
			this.field_3572 = new class_630(this, 32, 4);
			this.field_3572.method_2844(1.25F, 18.0F, -3.5F, 2, 2, 2);
			this.field_3570 = new class_630(this, 32, 8);
			this.field_3570.method_2844(0.0F, 21.0F, -3.5F, 1, 1, 1);
		} else {
			this.field_3571 = new class_630(this, 0, i);
			this.field_3571.method_2844(-4.0F, 16.0F, -4.0F, 8, 8, 8);
			this.field_3573 = null;
			this.field_3572 = null;
			this.field_3570 = null;
		}
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		GlStateManager.translatef(0.0F, 0.001F, 0.0F);
		this.field_3571.method_2846(k);
		if (this.field_3573 != null) {
			this.field_3573.method_2846(k);
			this.field_3572.method_2846(k);
			this.field_3570.method_2846(k);
		}
	}
}
