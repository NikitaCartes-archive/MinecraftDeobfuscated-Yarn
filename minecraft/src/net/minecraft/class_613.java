package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_613 extends class_3879 {
	public static final class_2960 field_3592 = new class_2960("textures/entity/trident.png");
	private final class_630 field_3593;

	public class_613() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		this.field_3593 = new class_630(this, 0, 0);
		this.field_3593.method_2856(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
		class_630 lv = new class_630(this, 4, 0);
		lv.method_2844(-1.5F, 0.0F, -0.5F, 3, 2, 1);
		this.field_3593.method_2845(lv);
		class_630 lv2 = new class_630(this, 4, 3);
		lv2.method_2844(-2.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_3593.method_2845(lv2);
		class_630 lv3 = new class_630(this, 4, 3);
		lv3.field_3666 = true;
		lv3.method_2844(1.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_3593.method_2845(lv3);
	}

	public void method_2835() {
		this.field_3593.method_2846(0.0625F);
	}
}
