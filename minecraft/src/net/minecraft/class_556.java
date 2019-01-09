package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_556 extends class_3879 {
	protected class_630 field_3330 = new class_630(this, 0, 0).method_2853(64, 64);
	protected class_630 field_3332;
	protected class_630 field_3331;

	public class_556() {
		this.field_3330.method_2856(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
		this.field_3330.field_3657 = 1.0F;
		this.field_3330.field_3656 = 7.0F;
		this.field_3330.field_3655 = 15.0F;
		this.field_3331 = new class_630(this, 0, 0).method_2853(64, 64);
		this.field_3331.method_2856(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.field_3331.field_3657 = 8.0F;
		this.field_3331.field_3656 = 7.0F;
		this.field_3331.field_3655 = 15.0F;
		this.field_3332 = new class_630(this, 0, 19).method_2853(64, 64);
		this.field_3332.method_2856(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
		this.field_3332.field_3657 = 1.0F;
		this.field_3332.field_3656 = 6.0F;
		this.field_3332.field_3655 = 1.0F;
	}

	public void method_2799() {
		this.field_3331.field_3654 = this.field_3330.field_3654;
		this.field_3330.method_2846(0.0625F);
		this.field_3331.method_2846(0.0625F);
		this.field_3332.method_2846(0.0625F);
	}

	public class_630 method_2798() {
		return this.field_3330;
	}
}
