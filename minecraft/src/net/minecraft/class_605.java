package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_605 extends class_3879 {
	private final class_630 field_3562 = new class_630(this, 0, 0);
	private final class_630 field_3563;

	public class_605() {
		this.field_3562.method_2856(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
		this.field_3563 = new class_630(this, 0, 14);
		this.field_3563.method_2856(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
	}

	public void method_2833() {
		this.field_3562.method_2846(0.0625F);
		this.field_3563.method_2846(0.0625F);
	}

	public class_630 method_2832() {
		return this.field_3563;
	}
}
