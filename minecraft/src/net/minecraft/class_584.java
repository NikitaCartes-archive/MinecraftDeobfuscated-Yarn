package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_584 extends class_583<class_1453> {
	private final class_630 field_3458;
	private final class_630 field_3460;
	private final class_630 field_3459;
	private final class_630 field_3455;
	private final class_630 field_3452;
	private final class_630 field_3461;
	private final class_630 field_3451;
	private final class_630 field_3453;
	private final class_630 field_3456;
	private final class_630 field_3450;
	private final class_630 field_3457;

	public class_584() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		this.field_3458 = new class_630(this, 2, 8);
		this.field_3458.method_2844(-1.5F, 0.0F, -1.5F, 3, 6, 3);
		this.field_3458.method_2851(0.0F, 16.5F, -3.0F);
		this.field_3460 = new class_630(this, 22, 1);
		this.field_3460.method_2844(-1.5F, -1.0F, -1.0F, 3, 4, 1);
		this.field_3460.method_2851(0.0F, 21.07F, 1.16F);
		this.field_3459 = new class_630(this, 19, 8);
		this.field_3459.method_2844(-0.5F, 0.0F, -1.5F, 1, 5, 3);
		this.field_3459.method_2851(1.5F, 16.94F, -2.76F);
		this.field_3455 = new class_630(this, 19, 8);
		this.field_3455.method_2844(-0.5F, 0.0F, -1.5F, 1, 5, 3);
		this.field_3455.method_2851(-1.5F, 16.94F, -2.76F);
		this.field_3452 = new class_630(this, 2, 2);
		this.field_3452.method_2844(-1.0F, -1.5F, -1.0F, 2, 3, 2);
		this.field_3452.method_2851(0.0F, 15.69F, -2.76F);
		this.field_3461 = new class_630(this, 10, 0);
		this.field_3461.method_2844(-1.0F, -0.5F, -2.0F, 2, 1, 4);
		this.field_3461.method_2851(0.0F, -2.0F, -1.0F);
		this.field_3452.method_2845(this.field_3461);
		this.field_3451 = new class_630(this, 11, 7);
		this.field_3451.method_2844(-0.5F, -1.0F, -0.5F, 1, 2, 1);
		this.field_3451.method_2851(0.0F, -0.5F, -1.5F);
		this.field_3452.method_2845(this.field_3451);
		this.field_3453 = new class_630(this, 16, 7);
		this.field_3453.method_2844(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_3453.method_2851(0.0F, -1.75F, -2.45F);
		this.field_3452.method_2845(this.field_3453);
		this.field_3456 = new class_630(this, 2, 18);
		this.field_3456.method_2844(0.0F, -4.0F, -2.0F, 0, 5, 4);
		this.field_3456.method_2851(0.0F, -2.15F, 0.15F);
		this.field_3452.method_2845(this.field_3456);
		this.field_3450 = new class_630(this, 14, 18);
		this.field_3450.method_2844(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_3450.method_2851(1.0F, 22.0F, -1.05F);
		this.field_3457 = new class_630(this, 14, 18);
		this.field_3457.method_2844(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_3457.method_2851(-1.0F, 22.0F, -1.05F);
	}

	public void method_17109(class_1453 arg, float f, float g, float h, float i, float j, float k) {
		this.method_17105(k);
	}

	public void method_17112(class_1453 arg, float f, float g, float h, float i, float j, float k) {
		this.method_17111(method_17107(arg), arg.field_6012, f, g, h, i, j);
	}

	public void method_17108(class_1453 arg, float f, float g, float h) {
		this.method_17110(method_17107(arg));
	}

	public void method_17106(float f, float g, float h, float i, float j, int k) {
		this.method_17110(class_584.class_585.field_3464);
		this.method_17111(class_584.class_585.field_3464, k, f, g, 0.0F, h, i);
		this.method_17105(j);
	}

	private void method_17105(float f) {
		this.field_3458.method_2846(f);
		this.field_3459.method_2846(f);
		this.field_3455.method_2846(f);
		this.field_3460.method_2846(f);
		this.field_3452.method_2846(f);
		this.field_3450.method_2846(f);
		this.field_3457.method_2846(f);
	}

	private void method_17111(class_584.class_585 arg, int i, float f, float g, float h, float j, float k) {
		this.field_3452.field_3654 = k * (float) (Math.PI / 180.0);
		this.field_3452.field_3675 = j * (float) (Math.PI / 180.0);
		this.field_3452.field_3674 = 0.0F;
		this.field_3452.field_3657 = 0.0F;
		this.field_3458.field_3657 = 0.0F;
		this.field_3460.field_3657 = 0.0F;
		this.field_3455.field_3657 = -1.5F;
		this.field_3459.field_3657 = 1.5F;
		switch (arg) {
			case field_3466:
				break;
			case field_3463:
				float l = class_3532.method_15362((float)i);
				float m = class_3532.method_15374((float)i);
				this.field_3452.field_3657 = l;
				this.field_3452.field_3656 = 15.69F + m;
				this.field_3452.field_3654 = 0.0F;
				this.field_3452.field_3675 = 0.0F;
				this.field_3452.field_3674 = class_3532.method_15374((float)i) * 0.4F;
				this.field_3458.field_3657 = l;
				this.field_3458.field_3656 = 16.5F + m;
				this.field_3459.field_3674 = -0.0873F - h;
				this.field_3459.field_3657 = 1.5F + l;
				this.field_3459.field_3656 = 16.94F + m;
				this.field_3455.field_3674 = 0.0873F + h;
				this.field_3455.field_3657 = -1.5F + l;
				this.field_3455.field_3656 = 16.94F + m;
				this.field_3460.field_3657 = l;
				this.field_3460.field_3656 = 21.07F + m;
				break;
			case field_3465:
				this.field_3450.field_3654 = this.field_3450.field_3654 + class_3532.method_15362(f * 0.6662F) * 1.4F * g;
				this.field_3457.field_3654 = this.field_3457.field_3654 + class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			case field_3462:
			case field_3464:
			default:
				float n = h * 0.3F;
				this.field_3452.field_3656 = 15.69F + n;
				this.field_3460.field_3654 = 1.015F + class_3532.method_15362(f * 0.6662F) * 0.3F * g;
				this.field_3460.field_3656 = 21.07F + n;
				this.field_3458.field_3656 = 16.5F + n;
				this.field_3459.field_3674 = -0.0873F - h;
				this.field_3459.field_3656 = 16.94F + n;
				this.field_3455.field_3674 = 0.0873F + h;
				this.field_3455.field_3656 = 16.94F + n;
				this.field_3450.field_3656 = 22.0F + n;
				this.field_3457.field_3656 = 22.0F + n;
		}
	}

	private void method_17110(class_584.class_585 arg) {
		this.field_3456.field_3654 = -0.2214F;
		this.field_3458.field_3654 = 0.4937F;
		this.field_3459.field_3654 = -0.6981F;
		this.field_3459.field_3675 = (float) -Math.PI;
		this.field_3455.field_3654 = -0.6981F;
		this.field_3455.field_3675 = (float) -Math.PI;
		this.field_3450.field_3654 = -0.0299F;
		this.field_3457.field_3654 = -0.0299F;
		this.field_3450.field_3656 = 22.0F;
		this.field_3457.field_3656 = 22.0F;
		this.field_3450.field_3674 = 0.0F;
		this.field_3457.field_3674 = 0.0F;
		switch (arg) {
			case field_3466:
				float f = 1.9F;
				this.field_3452.field_3656 = 17.59F;
				this.field_3460.field_3654 = 1.5388988F;
				this.field_3460.field_3656 = 22.97F;
				this.field_3458.field_3656 = 18.4F;
				this.field_3459.field_3674 = -0.0873F;
				this.field_3459.field_3656 = 18.84F;
				this.field_3455.field_3674 = 0.0873F;
				this.field_3455.field_3656 = 18.84F;
				this.field_3450.field_3656++;
				this.field_3457.field_3656++;
				this.field_3450.field_3654++;
				this.field_3457.field_3654++;
				break;
			case field_3463:
				this.field_3450.field_3674 = (float) (-Math.PI / 9);
				this.field_3457.field_3674 = (float) (Math.PI / 9);
			case field_3465:
			case field_3464:
			default:
				break;
			case field_3462:
				this.field_3450.field_3654 += (float) (Math.PI * 2.0 / 9.0);
				this.field_3457.field_3654 += (float) (Math.PI * 2.0 / 9.0);
		}
	}

	private static class_584.class_585 method_17107(class_1453 arg) {
		if (arg.method_6582()) {
			return class_584.class_585.field_3463;
		} else if (arg.method_6172()) {
			return class_584.class_585.field_3466;
		} else {
			return arg.method_6581() ? class_584.class_585.field_3462 : class_584.class_585.field_3465;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_585 {
		field_3462,
		field_3465,
		field_3466,
		field_3463,
		field_3464;
	}
}
