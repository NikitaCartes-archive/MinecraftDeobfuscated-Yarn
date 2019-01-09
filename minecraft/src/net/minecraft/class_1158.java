package net.minecraft;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class class_1158 {
	private final float[] field_5656;

	public class_1158() {
		this.field_5656 = new float[4];
		this.field_5656[4] = 1.0F;
	}

	public class_1158(float f, float g, float h, float i) {
		this.field_5656 = new float[4];
		this.field_5656[0] = f;
		this.field_5656[1] = g;
		this.field_5656[2] = h;
		this.field_5656[3] = i;
	}

	public class_1158(class_1160 arg, float f, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
		}

		float g = method_16002(f / 2.0F);
		this.field_5656 = new float[4];
		this.field_5656[0] = arg.method_4943() * g;
		this.field_5656[1] = arg.method_4945() * g;
		this.field_5656[2] = arg.method_4947() * g;
		this.field_5656[3] = method_16003(f / 2.0F);
	}

	public class_1158(float f, float g, float h, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
			g *= (float) (Math.PI / 180.0);
			h *= (float) (Math.PI / 180.0);
		}

		float i = method_16002(0.5F * f);
		float j = method_16003(0.5F * f);
		float k = method_16002(0.5F * g);
		float l = method_16003(0.5F * g);
		float m = method_16002(0.5F * h);
		float n = method_16003(0.5F * h);
		this.field_5656 = new float[4];
		this.field_5656[0] = i * l * n + j * k * m;
		this.field_5656[1] = j * k * n - i * l * m;
		this.field_5656[2] = i * k * n + j * l * m;
		this.field_5656[3] = j * l * n - i * k * m;
	}

	public class_1158(class_1158 arg) {
		this.field_5656 = Arrays.copyOf(arg.field_5656, 4);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_1158 lv = (class_1158)object;
			return Arrays.equals(this.field_5656, lv.field_5656);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_5656);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Quaternion[").append(this.method_4924()).append(" + ");
		stringBuilder.append(this.method_4921()).append("i + ");
		stringBuilder.append(this.method_4922()).append("j + ");
		stringBuilder.append(this.method_4923()).append("k]");
		return stringBuilder.toString();
	}

	public float method_4921() {
		return this.field_5656[0];
	}

	public float method_4922() {
		return this.field_5656[1];
	}

	public float method_4923() {
		return this.field_5656[2];
	}

	public float method_4924() {
		return this.field_5656[3];
	}

	public void method_4925(class_1158 arg) {
		float f = this.method_4921();
		float g = this.method_4922();
		float h = this.method_4923();
		float i = this.method_4924();
		float j = arg.method_4921();
		float k = arg.method_4922();
		float l = arg.method_4923();
		float m = arg.method_4924();
		this.field_5656[0] = i * j + f * m + g * l - h * k;
		this.field_5656[1] = i * k - f * l + g * m + h * j;
		this.field_5656[2] = i * l + f * k - g * j + h * m;
		this.field_5656[3] = i * m - f * j - g * k - h * l;
	}

	public void method_4926() {
		this.field_5656[0] = -this.field_5656[0];
		this.field_5656[1] = -this.field_5656[1];
		this.field_5656[2] = -this.field_5656[2];
	}

	private static float method_16003(float f) {
		return (float)Math.cos((double)f);
	}

	private static float method_16002(float f) {
		return (float)Math.sin((double)f);
	}
}
