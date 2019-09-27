package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(EnvType.CLIENT)
public final class class_4581 {
	private static final float field_20860 = 3.0F + 2.0F * (float)Math.sqrt(2.0);
	private static final float field_20861 = (float)Math.cos(Math.PI / 8);
	private static final float field_20862 = (float)Math.sin(Math.PI / 8);
	private static final float field_20863 = 1.0F / (float)Math.sqrt(2.0);
	private final float[] field_20864;

	public class_4581() {
		this.field_20864 = new float[9];
	}

	public class_4581(Quaternion quaternion) {
		this();
		float f = quaternion.getX();
		float g = quaternion.getY();
		float h = quaternion.getZ();
		float i = quaternion.getW();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.field_20864[0] = 1.0F - k - l;
		this.field_20864[4] = 1.0F - l - j;
		this.field_20864[8] = 1.0F - j - k;
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.field_20864[1] = 2.0F * (m + r);
		this.field_20864[3] = 2.0F * (m - r);
		this.field_20864[2] = 2.0F * (o - q);
		this.field_20864[6] = 2.0F * (o + q);
		this.field_20864[5] = 2.0F * (n + p);
		this.field_20864[7] = 2.0F * (n - p);
	}

	public class_4581(class_4581 arg, boolean bl) {
		this(arg.field_20864, true);
	}

	public class_4581(Matrix4f matrix4f) {
		this();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.field_20864[j + i * 3] = matrix4f.method_22669(j, i);
			}
		}
	}

	public class_4581(float[] fs, boolean bl) {
		if (bl) {
			this.field_20864 = new float[9];

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					this.field_20864[j + i * 3] = fs[i + j * 3];
				}
			}
		} else {
			this.field_20864 = Arrays.copyOf(fs, fs.length);
		}
	}

	public class_4581(class_4581 arg) {
		this.field_20864 = Arrays.copyOf(arg.field_20864, 9);
	}

	private static Pair<Float, Float> method_22849(float f, float g, float h) {
		float i = 2.0F * (f - h);
		if (field_20860 * g * g < i * i) {
			float k = MathHelper.method_22858(g * g + i * i);
			return Pair.of(k * g, k * i);
		} else {
			return Pair.of(field_20862, field_20861);
		}
	}

	private static Pair<Float, Float> method_22848(float f, float g) {
		float h = (float)Math.hypot((double)f, (double)g);
		float i = h > 1.0E-6F ? g : 0.0F;
		float j = Math.abs(f) + Math.max(h, 1.0E-6F);
		if (f < 0.0F) {
			float k = i;
			i = j;
			j = k;
		}

		float k = MathHelper.method_22858(j * j + i * i);
		j *= k;
		i *= k;
		return Pair.of(i, j);
	}

	private static Quaternion method_22857(class_4581 arg) {
		class_4581 lv = new class_4581();
		Quaternion quaternion = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		if (arg.method_22850(0, 1) * arg.method_22850(0, 1) + arg.method_22850(1, 0) * arg.method_22850(1, 0) > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(arg.method_22850(0, 0), 0.5F * (arg.method_22850(0, 1) + arg.method_22850(1, 0)), arg.method_22850(1, 1));
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, 0.0F, float_, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.copyFrom(quaternion2);
			lv.method_22856();
			lv.method_22851(0, 0, f);
			lv.method_22851(1, 1, f);
			lv.method_22851(1, 0, -g);
			lv.method_22851(0, 1, g);
			lv.method_22851(2, 2, h);
			arg.method_22855(lv);
			lv.method_22847();
			lv.method_22855(arg);
			arg.method_22852(lv);
		}

		if (arg.method_22850(0, 2) * arg.method_22850(0, 2) + arg.method_22850(2, 0) * arg.method_22850(2, 0) > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(arg.method_22850(0, 0), 0.5F * (arg.method_22850(0, 2) + arg.method_22850(2, 0)), arg.method_22850(2, 2));
			float i = -pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, i, 0.0F, float2);
			float f = float2 * float2 - i * i;
			float g = -2.0F * i * float2;
			float h = float2 * float2 + i * i;
			quaternion.copyFrom(quaternion2);
			lv.method_22856();
			lv.method_22851(0, 0, f);
			lv.method_22851(2, 2, f);
			lv.method_22851(2, 0, g);
			lv.method_22851(0, 2, -g);
			lv.method_22851(1, 1, h);
			arg.method_22855(lv);
			lv.method_22847();
			lv.method_22855(arg);
			arg.method_22852(lv);
		}

		if (arg.method_22850(1, 2) * arg.method_22850(1, 2) + arg.method_22850(2, 1) * arg.method_22850(2, 1) > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(arg.method_22850(1, 1), 0.5F * (arg.method_22850(1, 2) + arg.method_22850(2, 1)), arg.method_22850(2, 2));
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(float_, 0.0F, 0.0F, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.copyFrom(quaternion2);
			lv.method_22856();
			lv.method_22851(1, 1, f);
			lv.method_22851(2, 2, f);
			lv.method_22851(2, 1, -g);
			lv.method_22851(1, 2, g);
			lv.method_22851(0, 0, h);
			arg.method_22855(lv);
			lv.method_22847();
			lv.method_22855(arg);
			arg.method_22852(lv);
		}

		return quaternion;
	}

	public void method_22847() {
		this.method_22854(0, 1);
		this.method_22854(0, 2);
		this.method_22854(1, 2);
	}

	private void method_22854(int i, int j) {
		float f = this.field_20864[i + 3 * j];
		this.field_20864[i + 3 * j] = this.field_20864[j + 3 * i];
		this.field_20864[j + 3 * i] = f;
	}

	public Triple<Quaternion, Vector3f, Quaternion> method_22853() {
		Quaternion quaternion = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		Quaternion quaternion2 = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		class_4581 lv = new class_4581(this, true);
		lv.method_22855(this);

		for (int i = 0; i < 5; i++) {
			quaternion2.copyFrom(method_22857(lv));
		}

		quaternion2.method_22873();
		class_4581 lv2 = new class_4581(this);
		lv2.method_22855(new class_4581(quaternion2));
		float f = 1.0F;
		Pair<Float, Float> pair = method_22848(lv2.method_22850(0, 0), lv2.method_22850(1, 0));
		Float float_ = pair.getFirst();
		Float float2 = pair.getSecond();
		float g = float2 * float2 - float_ * float_;
		float h = -2.0F * float_ * float2;
		float j = float2 * float2 + float_ * float_;
		Quaternion quaternion3 = new Quaternion(0.0F, 0.0F, float_, float2);
		quaternion.copyFrom(quaternion3);
		class_4581 lv3 = new class_4581();
		lv3.method_22856();
		lv3.method_22851(0, 0, g);
		lv3.method_22851(1, 1, g);
		lv3.method_22851(1, 0, h);
		lv3.method_22851(0, 1, -h);
		lv3.method_22851(2, 2, j);
		f *= j;
		lv3.method_22855(lv2);
		pair = method_22848(lv3.method_22850(0, 0), lv3.method_22850(2, 0));
		float k = -pair.getFirst();
		Float float3 = pair.getSecond();
		float l = float3 * float3 - k * k;
		float m = -2.0F * k * float3;
		float n = float3 * float3 + k * k;
		Quaternion quaternion4 = new Quaternion(0.0F, k, 0.0F, float3);
		quaternion.copyFrom(quaternion4);
		class_4581 lv4 = new class_4581();
		lv4.method_22856();
		lv4.method_22851(0, 0, l);
		lv4.method_22851(2, 2, l);
		lv4.method_22851(2, 0, -m);
		lv4.method_22851(0, 2, m);
		lv4.method_22851(1, 1, n);
		f *= n;
		lv4.method_22855(lv3);
		pair = method_22848(lv4.method_22850(1, 1), lv4.method_22850(2, 1));
		Float float4 = pair.getFirst();
		Float float5 = pair.getSecond();
		float o = float5 * float5 - float4 * float4;
		float p = -2.0F * float4 * float5;
		float q = float5 * float5 + float4 * float4;
		Quaternion quaternion5 = new Quaternion(float4, 0.0F, 0.0F, float5);
		quaternion.copyFrom(quaternion5);
		class_4581 lv5 = new class_4581();
		lv5.method_22856();
		lv5.method_22851(1, 1, o);
		lv5.method_22851(2, 2, o);
		lv5.method_22851(2, 1, p);
		lv5.method_22851(1, 2, -p);
		lv5.method_22851(0, 0, q);
		f *= q;
		lv5.method_22855(lv4);
		f = 1.0F / f;
		quaternion.method_22872((float)Math.sqrt((double)f));
		Vector3f vector3f = new Vector3f(lv5.method_22850(0, 0) * f, lv5.method_22850(1, 1) * f, lv5.method_22850(2, 2) * f);
		return Triple.of(quaternion, vector3f, quaternion2);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4581 lv = (class_4581)object;
			return Arrays.equals(this.field_20864, lv.field_20864);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_20864);
	}

	public void method_22852(class_4581 arg) {
		System.arraycopy(arg.field_20864, 0, this.field_20864, 0, 9);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix3f:\n");

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				stringBuilder.append(this.field_20864[i + j * 3]);
				if (j != 2) {
					stringBuilder.append(" ");
				}
			}

			stringBuilder.append("\n");
		}

		return stringBuilder.toString();
	}

	public void method_22856() {
		this.field_20864[0] = 1.0F;
		this.field_20864[1] = 0.0F;
		this.field_20864[2] = 0.0F;
		this.field_20864[3] = 0.0F;
		this.field_20864[4] = 1.0F;
		this.field_20864[5] = 0.0F;
		this.field_20864[6] = 0.0F;
		this.field_20864[7] = 0.0F;
		this.field_20864[8] = 1.0F;
	}

	public float method_22850(int i, int j) {
		return this.field_20864[3 * j + i];
	}

	public void method_22851(int i, int j, float f) {
		this.field_20864[3 * j + i] = f;
	}

	public void method_22855(class_4581 arg) {
		float[] fs = Arrays.copyOf(this.field_20864, 9);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.field_20864[i + j * 3] = 0.0F;

				for (int k = 0; k < 3; k++) {
					this.field_20864[i + j * 3] = this.field_20864[i + j * 3] + fs[i + k * 3] * arg.field_20864[k + j * 3];
				}
			}
		}
	}
}
