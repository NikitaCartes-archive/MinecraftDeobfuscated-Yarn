package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_600 extends class_3879 {
	private final class_630 field_3550;
	private final class_630 field_3551;

	public class_600() {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3550 = new class_630(this, 0, 0);
		this.field_3550.method_2856(-6.0F, -11.0F, -2.0F, 12, 22, 1, 0.0F);
		this.field_3551 = new class_630(this, 26, 0);
		this.field_3551.method_2856(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
	}

	public void method_2828() {
		this.field_3550.method_2846(0.0625F);
		this.field_3551.method_2846(0.0625F);
	}
}
