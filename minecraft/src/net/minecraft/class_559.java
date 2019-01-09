package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_559<T extends class_1492> extends class_549<T> {
	private final class_630 field_3349 = new class_630(this, 26, 21);
	private final class_630 field_3348;

	public class_559() {
		this.field_3349.method_2844(-4.0F, 0.0F, -2.0F, 8, 8, 3);
		this.field_3348 = new class_630(this, 26, 21);
		this.field_3348.method_2844(-4.0F, 0.0F, -2.0F, 8, 8, 3);
		this.field_3349.field_3675 = (float) (-Math.PI / 2);
		this.field_3348.field_3675 = (float) (Math.PI / 2);
		this.field_3349.method_2851(6.0F, -8.0F, 0.0F);
		this.field_3348.method_2851(-6.0F, -8.0F, 0.0F);
		this.field_3305.method_2845(this.field_3349);
		this.field_3305.method_2845(this.field_3348);
	}

	@Override
	protected void method_2789(class_630 arg) {
		class_630 lv = new class_630(this, 0, 12);
		lv.method_2844(-1.0F, -7.0F, 0.0F, 2, 7, 1);
		lv.method_2851(1.25F, -10.0F, 4.0F);
		class_630 lv2 = new class_630(this, 0, 12);
		lv2.method_2844(-1.0F, -7.0F, 0.0F, 2, 7, 1);
		lv2.method_2851(-1.25F, -10.0F, 4.0F);
		lv.field_3654 = (float) (Math.PI / 12);
		lv.field_3674 = (float) (Math.PI / 12);
		lv2.field_3654 = (float) (Math.PI / 12);
		lv2.field_3674 = (float) (-Math.PI / 12);
		arg.method_2845(lv);
		arg.method_2845(lv2);
	}

	public void method_17076(T arg, float f, float g, float h, float i, float j, float k) {
		if (arg.method_6703()) {
			this.field_3349.field_3665 = true;
			this.field_3348.field_3665 = true;
		} else {
			this.field_3349.field_3665 = false;
			this.field_3348.field_3665 = false;
		}

		super.method_17085(arg, f, g, h, i, j, k);
	}
}
