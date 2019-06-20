package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_560<T extends class_1297> extends class_597<T> {
	public class_560() {
		super(12, 0.0F);
		this.field_3535 = new class_630(this, 0, 0);
		this.field_3535.method_2856(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
		this.field_3535.method_2851(0.0F, 4.0F, -8.0F);
		this.field_3535.method_2850(22, 0).method_2856(-5.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.field_3535.method_2850(22, 0).method_2856(4.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.field_3538 = new class_630(this, 18, 4);
		this.field_3538.method_2856(-6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F);
		this.field_3538.method_2851(0.0F, 5.0F, 2.0F);
		this.field_3538.method_2850(52, 0).method_2844(-2.0F, 2.0F, -8.0F, 4, 6, 1);
		this.field_3536.field_3657--;
		this.field_3534.field_3657++;
		this.field_3536.field_3655 += 0.0F;
		this.field_3534.field_3655 += 0.0F;
		this.field_3533.field_3657--;
		this.field_3539.field_3657++;
		this.field_3533.field_3655--;
		this.field_3539.field_3655--;
		this.field_3537 += 2.0F;
	}

	public class_630 method_2800() {
		return this.field_3535;
	}
}
