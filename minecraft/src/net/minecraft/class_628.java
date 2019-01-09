package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_628 {
	private final class_618[] field_3651;
	private final class_593[] field_3649;
	public final float field_3645;
	public final float field_3644;
	public final float field_3643;
	public final float field_3648;
	public final float field_3647;
	public final float field_3646;
	public String field_3650;

	public class_628(class_630 arg, int i, int j, float f, float g, float h, int k, int l, int m, float n) {
		this(arg, i, j, f, g, h, k, l, m, n, arg.field_3666);
	}

	public class_628(class_630 arg, int i, int j, float f, float g, float h, int k, int l, int m, float n, boolean bl) {
		this.field_3645 = f;
		this.field_3644 = g;
		this.field_3643 = h;
		this.field_3648 = f + (float)k;
		this.field_3647 = g + (float)l;
		this.field_3646 = h + (float)m;
		this.field_3651 = new class_618[8];
		this.field_3649 = new class_593[6];
		float o = f + (float)k;
		float p = g + (float)l;
		float q = h + (float)m;
		f -= n;
		g -= n;
		h -= n;
		o += n;
		p += n;
		q += n;
		if (bl) {
			float r = o;
			o = f;
			f = r;
		}

		class_618 lv = new class_618(f, g, h, 0.0F, 0.0F);
		class_618 lv2 = new class_618(o, g, h, 0.0F, 8.0F);
		class_618 lv3 = new class_618(o, p, h, 8.0F, 8.0F);
		class_618 lv4 = new class_618(f, p, h, 8.0F, 0.0F);
		class_618 lv5 = new class_618(f, g, q, 0.0F, 0.0F);
		class_618 lv6 = new class_618(o, g, q, 0.0F, 8.0F);
		class_618 lv7 = new class_618(o, p, q, 8.0F, 8.0F);
		class_618 lv8 = new class_618(f, p, q, 8.0F, 0.0F);
		this.field_3651[0] = lv;
		this.field_3651[1] = lv2;
		this.field_3651[2] = lv3;
		this.field_3651[3] = lv4;
		this.field_3651[4] = lv5;
		this.field_3651[5] = lv6;
		this.field_3651[6] = lv7;
		this.field_3651[7] = lv8;
		this.field_3649[0] = new class_593(new class_618[]{lv6, lv2, lv3, lv7}, i + m + k, j + m, i + m + k + m, j + m + l, arg.field_3659, arg.field_3658);
		this.field_3649[1] = new class_593(new class_618[]{lv, lv5, lv8, lv4}, i, j + m, i + m, j + m + l, arg.field_3659, arg.field_3658);
		this.field_3649[2] = new class_593(new class_618[]{lv6, lv5, lv, lv2}, i + m, j, i + m + k, j + m, arg.field_3659, arg.field_3658);
		this.field_3649[3] = new class_593(new class_618[]{lv3, lv4, lv8, lv7}, i + m + k, j + m, i + m + k + k, j, arg.field_3659, arg.field_3658);
		this.field_3649[4] = new class_593(new class_618[]{lv2, lv, lv4, lv3}, i + m, j + m, i + m + k, j + m + l, arg.field_3659, arg.field_3658);
		this.field_3649[5] = new class_593(new class_618[]{lv5, lv6, lv7, lv8}, i + m + k + m, j + m, i + m + k + m + k, j + m + l, arg.field_3659, arg.field_3658);
		if (bl) {
			for (class_593 lv9 : this.field_3649) {
				lv9.method_2826();
			}
		}
	}

	public void method_2843(class_287 arg, float f) {
		for (class_593 lv : this.field_3649) {
			lv.method_2825(arg, f);
		}
	}

	public class_628 method_2842(String string) {
		this.field_3650 = string;
		return this;
	}
}
