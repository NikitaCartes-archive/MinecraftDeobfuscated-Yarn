package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3878 extends class_3879 {
	private final class_630 field_17129;
	private final class_630 field_17130;

	public class_3878() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		this.field_17129 = new class_630(this, 0, 0);
		this.field_17129.method_2844(-3.0F, -6.0F, -3.0F, 6, 7, 6);
		this.field_17129.method_2851(8.0F, 12.0F, 8.0F);
		this.field_17130 = new class_630(this, 0, 13);
		this.field_17130.method_2844(4.0F, 4.0F, 4.0F, 8, 2, 8);
		this.field_17130.method_2851(-8.0F, -12.0F, -8.0F);
		this.field_17129.method_2845(this.field_17130);
	}

	public void method_17070(float f, float g, float h) {
		this.field_17129.field_3654 = f;
		this.field_17129.field_3674 = g;
		this.field_17129.method_2846(h);
	}
}
