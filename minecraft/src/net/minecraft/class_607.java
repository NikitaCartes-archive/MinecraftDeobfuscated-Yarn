package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_607 extends class_3879 {
	protected final class_630 field_3564;

	public class_607() {
		this(0, 35, 64, 64);
	}

	public class_607(int i, int j, int k, int l) {
		this.field_17138 = k;
		this.field_17139 = l;
		this.field_3564 = new class_630(this, i, j);
		this.field_3564.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.field_3564.method_2851(0.0F, 0.0F, 0.0F);
	}

	public void method_2821(float f, float g, float h, float i, float j, float k) {
		this.field_3564.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3564.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3564.method_2846(k);
	}
}
