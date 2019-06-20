package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_569 extends class_607 {
	private final class_630 field_3377 = new class_630(this, 32, 0);

	public class_569() {
		super(0, 0, 64, 64);
		this.field_3377.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
		this.field_3377.method_2851(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_2821(float f, float g, float h, float i, float j, float k) {
		super.method_2821(f, g, h, i, j, k);
		this.field_3377.field_3675 = this.field_3564.field_3675;
		this.field_3377.field_3654 = this.field_3564.field_3654;
		this.field_3377.method_2846(k);
	}
}
