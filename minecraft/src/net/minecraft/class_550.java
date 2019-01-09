package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_550 extends class_3879 {
	private final class_630 field_3309;
	private final class_630 field_3311;
	private final class_630 field_3310;

	public class_550() {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3309 = new class_630(this, 0, 0);
		this.field_3309.method_2856(-10.0F, 0.0F, -2.0F, 20, 40, 1, 0.0F);
		this.field_3311 = new class_630(this, 44, 0);
		this.field_3311.method_2856(-1.0F, -30.0F, -1.0F, 2, 42, 2, 0.0F);
		this.field_3310 = new class_630(this, 0, 42);
		this.field_3310.method_2856(-10.0F, -32.0F, -1.0F, 20, 2, 2, 0.0F);
	}

	public void method_2793() {
		this.field_3309.field_3656 = -32.0F;
		this.field_3309.method_2846(0.0625F);
		this.field_3311.method_2846(0.0625F);
		this.field_3310.method_2846(0.0625F);
	}

	public class_630 method_2791() {
		return this.field_3311;
	}

	public class_630 method_2792() {
		return this.field_3309;
	}
}
