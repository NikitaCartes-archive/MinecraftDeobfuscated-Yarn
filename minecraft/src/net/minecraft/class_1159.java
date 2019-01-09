package net.minecraft;

import java.nio.FloatBuffer;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class class_1159 {
	private final float[] field_5657;

	public class_1159() {
		this.field_5657 = new float[16];
	}

	public class_1159(class_1158 arg) {
		this();
		float f = arg.method_4921();
		float g = arg.method_4922();
		float h = arg.method_4923();
		float i = arg.method_4924();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.field_5657[0] = 1.0F - k - l;
		this.field_5657[5] = 1.0F - l - j;
		this.field_5657[10] = 1.0F - j - k;
		this.field_5657[15] = 1.0F;
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.field_5657[1] = 2.0F * (m + r);
		this.field_5657[4] = 2.0F * (m - r);
		this.field_5657[2] = 2.0F * (o - q);
		this.field_5657[8] = 2.0F * (o + q);
		this.field_5657[6] = 2.0F * (n + p);
		this.field_5657[9] = 2.0F * (n - p);
	}

	public class_1159(float[] fs) {
		this(fs, false);
	}

	public class_1159(float[] fs, boolean bl) {
		if (bl) {
			this.field_5657 = new float[16];

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.field_5657[i * 4 + j] = fs[j * 4 + i];
				}
			}
		} else {
			this.field_5657 = Arrays.copyOf(fs, fs.length);
		}
	}

	public class_1159(class_1159 arg) {
		this.field_5657 = Arrays.copyOf(arg.field_5657, 16);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_1159 lv = (class_1159)object;
			return Arrays.equals(this.field_5657, lv.field_5657);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_5657);
	}

	public void method_4928(FloatBuffer floatBuffer) {
		this.method_4936(floatBuffer, false);
	}

	public void method_4936(FloatBuffer floatBuffer, boolean bl) {
		if (bl) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.field_5657[i * 4 + j] = floatBuffer.get(j * 4 + i);
				}
			}
		} else {
			floatBuffer.get(this.field_5657);
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix4f:\n");

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				stringBuilder.append(this.field_5657[i + j * 4]);
				if (j != 3) {
					stringBuilder.append(" ");
				}
			}

			stringBuilder.append("\n");
		}

		return stringBuilder.toString();
	}

	public void method_4932(FloatBuffer floatBuffer) {
		this.method_4927(floatBuffer, false);
	}

	public void method_4927(FloatBuffer floatBuffer, boolean bl) {
		if (bl) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					floatBuffer.put(j * 4 + i, this.field_5657[i * 4 + j]);
				}
			}
		} else {
			floatBuffer.put(this.field_5657);
		}
	}

	public void method_4937() {
		this.field_5657[0] = 1.0F;
		this.field_5657[1] = 0.0F;
		this.field_5657[2] = 0.0F;
		this.field_5657[3] = 0.0F;
		this.field_5657[4] = 0.0F;
		this.field_5657[5] = 1.0F;
		this.field_5657[6] = 0.0F;
		this.field_5657[7] = 0.0F;
		this.field_5657[8] = 0.0F;
		this.field_5657[9] = 0.0F;
		this.field_5657[10] = 1.0F;
		this.field_5657[11] = 0.0F;
		this.field_5657[12] = 0.0F;
		this.field_5657[13] = 0.0F;
		this.field_5657[14] = 0.0F;
		this.field_5657[15] = 1.0F;
	}

	public float method_4938(int i, int j) {
		return this.field_5657[i + 4 * j];
	}

	public void method_4931(int i, int j, float f) {
		this.field_5657[i + 4 * j] = f;
	}

	public void method_4935(class_1159 arg) {
		float[] fs = Arrays.copyOf(this.field_5657, 16);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.field_5657[i + j * 4] = 0.0F;

				for (int k = 0; k < 4; k++) {
					this.field_5657[i + j * 4] = this.field_5657[i + j * 4] + fs[i + k * 4] * arg.field_5657[k + j * 4];
				}
			}
		}
	}

	public void method_4930(float f) {
		for (int i = 0; i < 16; i++) {
			this.field_5657[i] = this.field_5657[i] * f;
		}
	}

	public void method_4939(class_1159 arg) {
		for (int i = 0; i < 16; i++) {
			this.field_5657[i] = this.field_5657[i] + arg.field_5657[i];
		}
	}

	public void method_4940(class_1159 arg) {
		for (int i = 0; i < 16; i++) {
			this.field_5657[i] = this.field_5657[i] - arg.field_5657[i];
		}
	}

	public float method_4934() {
		float f = 0.0F;

		for (int i = 0; i < 4; i++) {
			f += this.field_5657[i + 4 * i];
		}

		return f;
	}

	public void method_4941() {
		class_1159 lv = new class_1159();
		class_1159 lv2 = new class_1159(this);
		class_1159 lv3 = new class_1159(this);
		lv2.method_4935(this);
		lv3.method_4935(lv2);
		float f = this.method_4934();
		float g = lv2.method_4934();
		float h = lv3.method_4934();
		this.method_4930((g - f * f) / 2.0F);
		lv.method_4937();
		lv.method_4930((f * f * f - 3.0F * f * g + 2.0F * h) / 6.0F);
		this.method_4939(lv);
		lv2.method_4930(f);
		this.method_4939(lv2);
		this.method_4940(lv3);
	}

	public static class_1159 method_4929(double d, float f, float g, float h) {
		float i = (float)(1.0 / Math.tan(d * (float) (Math.PI / 180.0) / 2.0));
		class_1159 lv = new class_1159();
		lv.method_4931(0, 0, i / f);
		lv.method_4931(1, 1, i);
		lv.method_4931(2, 2, (h + g) / (g - h));
		lv.method_4931(3, 2, -1.0F);
		lv.method_4931(2, 3, 2.0F * h * g / (g - h));
		return lv;
	}

	public static class_1159 method_4933(float f, float g, float h, float i) {
		class_1159 lv = new class_1159();
		lv.method_4931(0, 0, 2.0F / f);
		lv.method_4931(1, 1, 2.0F / g);
		float j = i - h;
		lv.method_4931(2, 2, -2.0F / j);
		lv.method_4931(3, 3, 1.0F);
		lv.method_4931(0, 3, -1.0F);
		lv.method_4931(1, 3, -1.0F);
		lv.method_4931(2, 3, -(i + h) / j);
		return lv;
	}
}
