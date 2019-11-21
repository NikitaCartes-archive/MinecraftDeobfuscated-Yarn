package net.minecraft.client.util.math;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(EnvType.CLIENT)
public final class Matrix3f {
	private static final float THREE_PLUS_TWO_SQRT_TWO = 3.0F + 2.0F * (float)Math.sqrt(2.0);
	private static final float COS_PI_OVER_EIGHT = (float)Math.cos(Math.PI / 8);
	private static final float SIN_PI_OVER_EIGHT = (float)Math.sin(Math.PI / 8);
	private static final float SQRT_HALF = 1.0F / (float)Math.sqrt(2.0);
	protected float field_21633;
	protected float field_21634;
	protected float field_21635;
	protected float field_21636;
	protected float field_21637;
	protected float field_21638;
	protected float field_21639;
	protected float field_21640;
	protected float field_21641;

	public Matrix3f() {
	}

	public Matrix3f(Quaternion quaternion) {
		float f = quaternion.getB();
		float g = quaternion.getC();
		float h = quaternion.getD();
		float i = quaternion.getA();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.field_21633 = 1.0F - k - l;
		this.field_21637 = 1.0F - l - j;
		this.field_21641 = 1.0F - j - k;
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.field_21636 = 2.0F * (m + r);
		this.field_21634 = 2.0F * (m - r);
		this.field_21639 = 2.0F * (o - q);
		this.field_21635 = 2.0F * (o + q);
		this.field_21640 = 2.0F * (n + p);
		this.field_21638 = 2.0F * (n - p);
	}

	public static Matrix3f method_23963(float f, float g, float h) {
		Matrix3f matrix3f = new Matrix3f();
		matrix3f.field_21633 = f;
		matrix3f.field_21637 = g;
		matrix3f.field_21641 = h;
		return matrix3f;
	}

	public Matrix3f(Matrix4f matrix4f) {
		this.field_21633 = matrix4f.field_21652;
		this.field_21634 = matrix4f.field_21653;
		this.field_21635 = matrix4f.field_21654;
		this.field_21636 = matrix4f.field_21656;
		this.field_21637 = matrix4f.field_21657;
		this.field_21638 = matrix4f.field_21658;
		this.field_21639 = matrix4f.field_21660;
		this.field_21640 = matrix4f.field_21661;
		this.field_21641 = matrix4f.field_21662;
	}

	public Matrix3f(Matrix3f matrix3f) {
		this.field_21633 = matrix3f.field_21633;
		this.field_21634 = matrix3f.field_21634;
		this.field_21635 = matrix3f.field_21635;
		this.field_21636 = matrix3f.field_21636;
		this.field_21637 = matrix3f.field_21637;
		this.field_21638 = matrix3f.field_21638;
		this.field_21639 = matrix3f.field_21639;
		this.field_21640 = matrix3f.field_21640;
		this.field_21641 = matrix3f.field_21641;
	}

	private static Pair<Float, Float> method_22849(float f, float g, float h) {
		float i = 2.0F * (f - h);
		if (THREE_PLUS_TWO_SQRT_TWO * g * g < i * i) {
			float k = MathHelper.fastInverseSqrt(g * g + i * i);
			return Pair.of(k * g, k * i);
		} else {
			return Pair.of(SIN_PI_OVER_EIGHT, COS_PI_OVER_EIGHT);
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

		float k = MathHelper.fastInverseSqrt(j * j + i * i);
		j *= k;
		i *= k;
		return Pair.of(i, j);
	}

	private static Quaternion method_22857(Matrix3f matrix3f) {
		Matrix3f matrix3f2 = new Matrix3f();
		Quaternion quaternion = Quaternion.IDENTITY.copy();
		if (matrix3f.field_21634 * matrix3f.field_21634 + matrix3f.field_21636 * matrix3f.field_21636 > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(matrix3f.field_21633, 0.5F * (matrix3f.field_21634 + matrix3f.field_21636), matrix3f.field_21637);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, 0.0F, float_, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.field_21633 = f;
			matrix3f2.field_21637 = f;
			matrix3f2.field_21636 = -g;
			matrix3f2.field_21634 = g;
			matrix3f2.field_21641 = h;
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		if (matrix3f.field_21635 * matrix3f.field_21635 + matrix3f.field_21639 * matrix3f.field_21639 > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(matrix3f.field_21633, 0.5F * (matrix3f.field_21635 + matrix3f.field_21639), matrix3f.field_21641);
			float i = -pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, i, 0.0F, float2);
			float f = float2 * float2 - i * i;
			float g = -2.0F * i * float2;
			float h = float2 * float2 + i * i;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.field_21633 = f;
			matrix3f2.field_21641 = f;
			matrix3f2.field_21639 = g;
			matrix3f2.field_21635 = -g;
			matrix3f2.field_21637 = h;
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		if (matrix3f.field_21638 * matrix3f.field_21638 + matrix3f.field_21640 * matrix3f.field_21640 > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(matrix3f.field_21637, 0.5F * (matrix3f.field_21638 + matrix3f.field_21640), matrix3f.field_21641);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(float_, 0.0F, 0.0F, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.field_21637 = f;
			matrix3f2.field_21641 = f;
			matrix3f2.field_21640 = -g;
			matrix3f2.field_21638 = g;
			matrix3f2.field_21633 = h;
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		return quaternion;
	}

	public void transpose() {
		float f = this.field_21634;
		this.field_21634 = this.field_21636;
		this.field_21636 = f;
		f = this.field_21635;
		this.field_21635 = this.field_21639;
		this.field_21639 = f;
		f = this.field_21638;
		this.field_21638 = this.field_21640;
		this.field_21640 = f;
	}

	public Triple<Quaternion, Vector3f, Quaternion> method_22853() {
		Quaternion quaternion = Quaternion.IDENTITY.copy();
		Quaternion quaternion2 = Quaternion.IDENTITY.copy();
		Matrix3f matrix3f = this.copy();
		matrix3f.transpose();
		matrix3f.multiply(this);

		for (int i = 0; i < 5; i++) {
			quaternion2.hamiltonProduct(method_22857(matrix3f));
		}

		quaternion2.normalize();
		Matrix3f matrix3f2 = new Matrix3f(this);
		matrix3f2.multiply(new Matrix3f(quaternion2));
		float f = 1.0F;
		Pair<Float, Float> pair = method_22848(matrix3f2.field_21633, matrix3f2.field_21636);
		Float float_ = pair.getFirst();
		Float float2 = pair.getSecond();
		float g = float2 * float2 - float_ * float_;
		float h = -2.0F * float_ * float2;
		float j = float2 * float2 + float_ * float_;
		Quaternion quaternion3 = new Quaternion(0.0F, 0.0F, float_, float2);
		quaternion.hamiltonProduct(quaternion3);
		Matrix3f matrix3f3 = new Matrix3f();
		matrix3f3.loadIdentity();
		matrix3f3.field_21633 = g;
		matrix3f3.field_21637 = g;
		matrix3f3.field_21636 = h;
		matrix3f3.field_21634 = -h;
		matrix3f3.field_21641 = j;
		f *= j;
		matrix3f3.multiply(matrix3f2);
		pair = method_22848(matrix3f3.field_21633, matrix3f3.field_21639);
		float k = -pair.getFirst();
		Float float3 = pair.getSecond();
		float l = float3 * float3 - k * k;
		float m = -2.0F * k * float3;
		float n = float3 * float3 + k * k;
		Quaternion quaternion4 = new Quaternion(0.0F, k, 0.0F, float3);
		quaternion.hamiltonProduct(quaternion4);
		Matrix3f matrix3f4 = new Matrix3f();
		matrix3f4.loadIdentity();
		matrix3f4.field_21633 = l;
		matrix3f4.field_21641 = l;
		matrix3f4.field_21639 = -m;
		matrix3f4.field_21635 = m;
		matrix3f4.field_21637 = n;
		f *= n;
		matrix3f4.multiply(matrix3f3);
		pair = method_22848(matrix3f4.field_21637, matrix3f4.field_21640);
		Float float4 = pair.getFirst();
		Float float5 = pair.getSecond();
		float o = float5 * float5 - float4 * float4;
		float p = -2.0F * float4 * float5;
		float q = float5 * float5 + float4 * float4;
		Quaternion quaternion5 = new Quaternion(float4, 0.0F, 0.0F, float5);
		quaternion.hamiltonProduct(quaternion5);
		Matrix3f matrix3f5 = new Matrix3f();
		matrix3f5.loadIdentity();
		matrix3f5.field_21637 = o;
		matrix3f5.field_21641 = o;
		matrix3f5.field_21640 = p;
		matrix3f5.field_21638 = -p;
		matrix3f5.field_21633 = q;
		f *= q;
		matrix3f5.multiply(matrix3f4);
		f = 1.0F / f;
		quaternion.scale((float)Math.sqrt((double)f));
		Vector3f vector3f = new Vector3f(matrix3f5.field_21633 * f, matrix3f5.field_21637 * f, matrix3f5.field_21641 * f);
		return Triple.of(quaternion, vector3f, quaternion2);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Matrix3f matrix3f = (Matrix3f)object;
			return Float.compare(matrix3f.field_21633, this.field_21633) == 0
				&& Float.compare(matrix3f.field_21634, this.field_21634) == 0
				&& Float.compare(matrix3f.field_21635, this.field_21635) == 0
				&& Float.compare(matrix3f.field_21636, this.field_21636) == 0
				&& Float.compare(matrix3f.field_21637, this.field_21637) == 0
				&& Float.compare(matrix3f.field_21638, this.field_21638) == 0
				&& Float.compare(matrix3f.field_21639, this.field_21639) == 0
				&& Float.compare(matrix3f.field_21640, this.field_21640) == 0
				&& Float.compare(matrix3f.field_21641, this.field_21641) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.field_21633 != 0.0F ? Float.floatToIntBits(this.field_21633) : 0;
		i = 31 * i + (this.field_21634 != 0.0F ? Float.floatToIntBits(this.field_21634) : 0);
		i = 31 * i + (this.field_21635 != 0.0F ? Float.floatToIntBits(this.field_21635) : 0);
		i = 31 * i + (this.field_21636 != 0.0F ? Float.floatToIntBits(this.field_21636) : 0);
		i = 31 * i + (this.field_21637 != 0.0F ? Float.floatToIntBits(this.field_21637) : 0);
		i = 31 * i + (this.field_21638 != 0.0F ? Float.floatToIntBits(this.field_21638) : 0);
		i = 31 * i + (this.field_21639 != 0.0F ? Float.floatToIntBits(this.field_21639) : 0);
		i = 31 * i + (this.field_21640 != 0.0F ? Float.floatToIntBits(this.field_21640) : 0);
		return 31 * i + (this.field_21641 != 0.0F ? Float.floatToIntBits(this.field_21641) : 0);
	}

	public void load(Matrix3f matrix3f) {
		this.field_21633 = matrix3f.field_21633;
		this.field_21634 = matrix3f.field_21634;
		this.field_21635 = matrix3f.field_21635;
		this.field_21636 = matrix3f.field_21636;
		this.field_21637 = matrix3f.field_21637;
		this.field_21638 = matrix3f.field_21638;
		this.field_21639 = matrix3f.field_21639;
		this.field_21640 = matrix3f.field_21640;
		this.field_21641 = matrix3f.field_21641;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix3f:\n");
		stringBuilder.append(this.field_21633);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21634);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21635);
		stringBuilder.append("\n");
		stringBuilder.append(this.field_21636);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21637);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21638);
		stringBuilder.append("\n");
		stringBuilder.append(this.field_21639);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21640);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21641);
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	public void loadIdentity() {
		this.field_21633 = 1.0F;
		this.field_21634 = 0.0F;
		this.field_21635 = 0.0F;
		this.field_21636 = 0.0F;
		this.field_21637 = 1.0F;
		this.field_21638 = 0.0F;
		this.field_21639 = 0.0F;
		this.field_21640 = 0.0F;
		this.field_21641 = 1.0F;
	}

	public float method_23731() {
		float f = this.field_21637 * this.field_21641 - this.field_21638 * this.field_21640;
		float g = -(this.field_21636 * this.field_21641 - this.field_21638 * this.field_21639);
		float h = this.field_21636 * this.field_21640 - this.field_21637 * this.field_21639;
		float i = -(this.field_21634 * this.field_21641 - this.field_21635 * this.field_21640);
		float j = this.field_21633 * this.field_21641 - this.field_21635 * this.field_21639;
		float k = -(this.field_21633 * this.field_21640 - this.field_21634 * this.field_21639);
		float l = this.field_21634 * this.field_21638 - this.field_21635 * this.field_21637;
		float m = -(this.field_21633 * this.field_21638 - this.field_21635 * this.field_21636);
		float n = this.field_21633 * this.field_21637 - this.field_21634 * this.field_21636;
		float o = this.field_21633 * f + this.field_21634 * g + this.field_21635 * h;
		this.field_21633 = f;
		this.field_21636 = g;
		this.field_21639 = h;
		this.field_21634 = i;
		this.field_21637 = j;
		this.field_21640 = k;
		this.field_21635 = l;
		this.field_21638 = m;
		this.field_21641 = n;
		return o;
	}

	public boolean method_23732() {
		float f = this.method_23731();
		if (Math.abs(f) > 1.0E-6F) {
			this.method_23729(f);
			return true;
		} else {
			return false;
		}
	}

	public void multiply(Matrix3f matrix3f) {
		float f = this.field_21633 * matrix3f.field_21633 + this.field_21634 * matrix3f.field_21636 + this.field_21635 * matrix3f.field_21639;
		float g = this.field_21633 * matrix3f.field_21634 + this.field_21634 * matrix3f.field_21637 + this.field_21635 * matrix3f.field_21640;
		float h = this.field_21633 * matrix3f.field_21635 + this.field_21634 * matrix3f.field_21638 + this.field_21635 * matrix3f.field_21641;
		float i = this.field_21636 * matrix3f.field_21633 + this.field_21637 * matrix3f.field_21636 + this.field_21638 * matrix3f.field_21639;
		float j = this.field_21636 * matrix3f.field_21634 + this.field_21637 * matrix3f.field_21637 + this.field_21638 * matrix3f.field_21640;
		float k = this.field_21636 * matrix3f.field_21635 + this.field_21637 * matrix3f.field_21638 + this.field_21638 * matrix3f.field_21641;
		float l = this.field_21639 * matrix3f.field_21633 + this.field_21640 * matrix3f.field_21636 + this.field_21641 * matrix3f.field_21639;
		float m = this.field_21639 * matrix3f.field_21634 + this.field_21640 * matrix3f.field_21637 + this.field_21641 * matrix3f.field_21640;
		float n = this.field_21639 * matrix3f.field_21635 + this.field_21640 * matrix3f.field_21638 + this.field_21641 * matrix3f.field_21641;
		this.field_21633 = f;
		this.field_21634 = g;
		this.field_21635 = h;
		this.field_21636 = i;
		this.field_21637 = j;
		this.field_21638 = k;
		this.field_21639 = l;
		this.field_21640 = m;
		this.field_21641 = n;
	}

	public void multiply(Quaternion quaternion) {
		this.multiply(new Matrix3f(quaternion));
	}

	public void method_23729(float f) {
		this.field_21633 *= f;
		this.field_21634 *= f;
		this.field_21635 *= f;
		this.field_21636 *= f;
		this.field_21637 *= f;
		this.field_21638 *= f;
		this.field_21639 *= f;
		this.field_21640 *= f;
		this.field_21641 *= f;
	}

	public Matrix3f copy() {
		return new Matrix3f(this);
	}
}
