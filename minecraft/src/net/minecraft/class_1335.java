package net.minecraft;

public class class_1335 {
	protected final class_1308 field_6371;
	protected double field_6370;
	protected double field_6369;
	protected double field_6367;
	protected double field_6372;
	protected float field_6368;
	protected float field_6373;
	protected class_1335.class_1336 field_6374 = class_1335.class_1336.field_6377;

	public class_1335(class_1308 arg) {
		this.field_6371 = arg;
	}

	public boolean method_6241() {
		return this.field_6374 == class_1335.class_1336.field_6378;
	}

	public double method_6242() {
		return this.field_6372;
	}

	public void method_6239(double d, double e, double f, double g) {
		this.field_6370 = d;
		this.field_6369 = e;
		this.field_6367 = f;
		this.field_6372 = g;
		if (this.field_6374 != class_1335.class_1336.field_6379) {
			this.field_6374 = class_1335.class_1336.field_6378;
		}
	}

	public void method_6243(float f, float g) {
		this.field_6374 = class_1335.class_1336.field_6376;
		this.field_6368 = f;
		this.field_6373 = g;
		this.field_6372 = 0.25;
	}

	public void method_6240() {
		if (this.field_6374 == class_1335.class_1336.field_6376) {
			float f = (float)this.field_6371.method_5996(class_1612.field_7357).method_6194();
			float g = (float)this.field_6372 * f;
			float h = this.field_6368;
			float i = this.field_6373;
			float j = class_3532.method_15355(h * h + i * i);
			if (j < 1.0F) {
				j = 1.0F;
			}

			j = g / j;
			h *= j;
			i *= j;
			float k = class_3532.method_15374(this.field_6371.field_6031 * (float) (Math.PI / 180.0));
			float l = class_3532.method_15362(this.field_6371.field_6031 * (float) (Math.PI / 180.0));
			float m = h * l - i * k;
			float n = i * l + h * k;
			class_1408 lv = this.field_6371.method_5942();
			if (lv != null) {
				class_8 lv2 = lv.method_6342();
				if (lv2 != null
					&& lv2.method_25(
							this.field_6371.field_6002,
							class_3532.method_15357(this.field_6371.field_5987 + (double)m),
							class_3532.method_15357(this.field_6371.field_6010),
							class_3532.method_15357(this.field_6371.field_6035 + (double)n)
						)
						!= class_7.field_12) {
					this.field_6368 = 1.0F;
					this.field_6373 = 0.0F;
					g = f;
				}
			}

			this.field_6371.method_6125(g);
			this.field_6371.method_5930(this.field_6368);
			this.field_6371.method_5938(this.field_6373);
			this.field_6374 = class_1335.class_1336.field_6377;
		} else if (this.field_6374 == class_1335.class_1336.field_6378) {
			this.field_6374 = class_1335.class_1336.field_6377;
			double d = this.field_6370 - this.field_6371.field_5987;
			double e = this.field_6367 - this.field_6371.field_6035;
			double o = this.field_6369 - this.field_6371.field_6010;
			double p = d * d + o * o + e * e;
			if (p < 2.5000003E-7F) {
				this.field_6371.method_5930(0.0F);
				return;
			}

			float n = (float)(class_3532.method_15349(e, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.field_6371.field_6031 = this.method_6238(this.field_6371.field_6031, n, 90.0F);
			this.field_6371.method_6125((float)(this.field_6372 * this.field_6371.method_5996(class_1612.field_7357).method_6194()));
			if (o > (double)this.field_6371.field_6013 && d * d + e * e < (double)Math.max(1.0F, this.field_6371.method_17681())) {
				this.field_6371.method_5993().method_6233();
				this.field_6374 = class_1335.class_1336.field_6379;
			}
		} else if (this.field_6374 == class_1335.class_1336.field_6379) {
			this.field_6371.method_6125((float)(this.field_6372 * this.field_6371.method_5996(class_1612.field_7357).method_6194()));
			if (this.field_6371.field_5952) {
				this.field_6374 = class_1335.class_1336.field_6377;
			}
		} else {
			this.field_6371.method_5930(0.0F);
		}
	}

	protected float method_6238(float f, float g, float h) {
		float i = class_3532.method_15393(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		float j = f + i;
		if (j < 0.0F) {
			j += 360.0F;
		} else if (j > 360.0F) {
			j -= 360.0F;
		}

		return j;
	}

	public double method_6236() {
		return this.field_6370;
	}

	public double method_6235() {
		return this.field_6369;
	}

	public double method_6237() {
		return this.field_6367;
	}

	public static enum class_1336 {
		field_6377,
		field_6378,
		field_6376,
		field_6379;
	}
}
