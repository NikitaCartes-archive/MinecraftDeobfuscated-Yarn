package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_601<T extends class_1472> extends class_597<T> {
	private float field_3552;

	public class_601() {
		super(12, 0.0F);
		this.field_3535 = new class_630(this, 0, 0);
		this.field_3535.method_2856(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
		this.field_3535.method_2851(0.0F, 6.0F, -8.0F);
		this.field_3538 = new class_630(this, 28, 8);
		this.field_3538.method_2856(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
		this.field_3538.method_2851(0.0F, 5.0F, 2.0F);
	}

	public void method_17120(T arg, float f, float g, float h) {
		super.method_2816(arg, f, g, h);
		this.field_3535.field_3656 = 6.0F + arg.method_6628(h) * 9.0F;
		this.field_3552 = arg.method_6641(h);
	}

	public void method_17121(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		this.field_3535.field_3654 = this.field_3552;
	}
}
