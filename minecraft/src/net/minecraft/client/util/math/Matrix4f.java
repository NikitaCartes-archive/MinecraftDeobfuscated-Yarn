package net.minecraft.client.util.math;

import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public final class Matrix4f {
	protected float field_21652;
	protected float field_21653;
	protected float field_21654;
	protected float field_21655;
	protected float field_21656;
	protected float field_21657;
	protected float field_21658;
	protected float field_21659;
	protected float field_21660;
	protected float field_21661;
	protected float field_21662;
	protected float field_21663;
	protected float field_21664;
	protected float field_21665;
	protected float field_21666;
	protected float field_21667;

	public Matrix4f() {
	}

	public Matrix4f(Matrix4f matrix4f) {
		this.field_21652 = matrix4f.field_21652;
		this.field_21653 = matrix4f.field_21653;
		this.field_21654 = matrix4f.field_21654;
		this.field_21655 = matrix4f.field_21655;
		this.field_21656 = matrix4f.field_21656;
		this.field_21657 = matrix4f.field_21657;
		this.field_21658 = matrix4f.field_21658;
		this.field_21659 = matrix4f.field_21659;
		this.field_21660 = matrix4f.field_21660;
		this.field_21661 = matrix4f.field_21661;
		this.field_21662 = matrix4f.field_21662;
		this.field_21663 = matrix4f.field_21663;
		this.field_21664 = matrix4f.field_21664;
		this.field_21665 = matrix4f.field_21665;
		this.field_21666 = matrix4f.field_21666;
		this.field_21667 = matrix4f.field_21667;
	}

	public Matrix4f(Quaternion quaternion) {
		float f = quaternion.getB();
		float g = quaternion.getC();
		float h = quaternion.getD();
		float i = quaternion.getA();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.field_21652 = 1.0F - k - l;
		this.field_21657 = 1.0F - l - j;
		this.field_21662 = 1.0F - j - k;
		this.field_21667 = 1.0F;
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.field_21656 = 2.0F * (m + r);
		this.field_21653 = 2.0F * (m - r);
		this.field_21660 = 2.0F * (o - q);
		this.field_21654 = 2.0F * (o + q);
		this.field_21661 = 2.0F * (n + p);
		this.field_21658 = 2.0F * (n - p);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Matrix4f matrix4f = (Matrix4f)o;
			return Float.compare(matrix4f.field_21652, this.field_21652) == 0
				&& Float.compare(matrix4f.field_21653, this.field_21653) == 0
				&& Float.compare(matrix4f.field_21654, this.field_21654) == 0
				&& Float.compare(matrix4f.field_21655, this.field_21655) == 0
				&& Float.compare(matrix4f.field_21656, this.field_21656) == 0
				&& Float.compare(matrix4f.field_21657, this.field_21657) == 0
				&& Float.compare(matrix4f.field_21658, this.field_21658) == 0
				&& Float.compare(matrix4f.field_21659, this.field_21659) == 0
				&& Float.compare(matrix4f.field_21660, this.field_21660) == 0
				&& Float.compare(matrix4f.field_21661, this.field_21661) == 0
				&& Float.compare(matrix4f.field_21662, this.field_21662) == 0
				&& Float.compare(matrix4f.field_21663, this.field_21663) == 0
				&& Float.compare(matrix4f.field_21664, this.field_21664) == 0
				&& Float.compare(matrix4f.field_21665, this.field_21665) == 0
				&& Float.compare(matrix4f.field_21666, this.field_21666) == 0
				&& Float.compare(matrix4f.field_21667, this.field_21667) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.field_21652 != 0.0F ? Float.floatToIntBits(this.field_21652) : 0;
		i = 31 * i + (this.field_21653 != 0.0F ? Float.floatToIntBits(this.field_21653) : 0);
		i = 31 * i + (this.field_21654 != 0.0F ? Float.floatToIntBits(this.field_21654) : 0);
		i = 31 * i + (this.field_21655 != 0.0F ? Float.floatToIntBits(this.field_21655) : 0);
		i = 31 * i + (this.field_21656 != 0.0F ? Float.floatToIntBits(this.field_21656) : 0);
		i = 31 * i + (this.field_21657 != 0.0F ? Float.floatToIntBits(this.field_21657) : 0);
		i = 31 * i + (this.field_21658 != 0.0F ? Float.floatToIntBits(this.field_21658) : 0);
		i = 31 * i + (this.field_21659 != 0.0F ? Float.floatToIntBits(this.field_21659) : 0);
		i = 31 * i + (this.field_21660 != 0.0F ? Float.floatToIntBits(this.field_21660) : 0);
		i = 31 * i + (this.field_21661 != 0.0F ? Float.floatToIntBits(this.field_21661) : 0);
		i = 31 * i + (this.field_21662 != 0.0F ? Float.floatToIntBits(this.field_21662) : 0);
		i = 31 * i + (this.field_21663 != 0.0F ? Float.floatToIntBits(this.field_21663) : 0);
		i = 31 * i + (this.field_21664 != 0.0F ? Float.floatToIntBits(this.field_21664) : 0);
		i = 31 * i + (this.field_21665 != 0.0F ? Float.floatToIntBits(this.field_21665) : 0);
		i = 31 * i + (this.field_21666 != 0.0F ? Float.floatToIntBits(this.field_21666) : 0);
		return 31 * i + (this.field_21667 != 0.0F ? Float.floatToIntBits(this.field_21667) : 0);
	}

	private static int method_24020(int i, int j) {
		return j * 4 + i;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix4f:\n");
		stringBuilder.append(this.field_21652);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21653);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21654);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21655);
		stringBuilder.append("\n");
		stringBuilder.append(this.field_21656);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21657);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21658);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21659);
		stringBuilder.append("\n");
		stringBuilder.append(this.field_21660);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21661);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21662);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21663);
		stringBuilder.append("\n");
		stringBuilder.append(this.field_21664);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21665);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21666);
		stringBuilder.append(" ");
		stringBuilder.append(this.field_21667);
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	public void writeToBuffer(FloatBuffer floatBuffer) {
		floatBuffer.put(method_24020(0, 0), this.field_21652);
		floatBuffer.put(method_24020(0, 1), this.field_21653);
		floatBuffer.put(method_24020(0, 2), this.field_21654);
		floatBuffer.put(method_24020(0, 3), this.field_21655);
		floatBuffer.put(method_24020(1, 0), this.field_21656);
		floatBuffer.put(method_24020(1, 1), this.field_21657);
		floatBuffer.put(method_24020(1, 2), this.field_21658);
		floatBuffer.put(method_24020(1, 3), this.field_21659);
		floatBuffer.put(method_24020(2, 0), this.field_21660);
		floatBuffer.put(method_24020(2, 1), this.field_21661);
		floatBuffer.put(method_24020(2, 2), this.field_21662);
		floatBuffer.put(method_24020(2, 3), this.field_21663);
		floatBuffer.put(method_24020(3, 0), this.field_21664);
		floatBuffer.put(method_24020(3, 1), this.field_21665);
		floatBuffer.put(method_24020(3, 2), this.field_21666);
		floatBuffer.put(method_24020(3, 3), this.field_21667);
	}

	public void loadIdentity() {
		this.field_21652 = 1.0F;
		this.field_21653 = 0.0F;
		this.field_21654 = 0.0F;
		this.field_21655 = 0.0F;
		this.field_21656 = 0.0F;
		this.field_21657 = 1.0F;
		this.field_21658 = 0.0F;
		this.field_21659 = 0.0F;
		this.field_21660 = 0.0F;
		this.field_21661 = 0.0F;
		this.field_21662 = 1.0F;
		this.field_21663 = 0.0F;
		this.field_21664 = 0.0F;
		this.field_21665 = 0.0F;
		this.field_21666 = 0.0F;
		this.field_21667 = 1.0F;
	}

	public float determinantAndAdjugate() {
		float f = this.field_21652 * this.field_21657 - this.field_21653 * this.field_21656;
		float g = this.field_21652 * this.field_21658 - this.field_21654 * this.field_21656;
		float h = this.field_21652 * this.field_21659 - this.field_21655 * this.field_21656;
		float i = this.field_21653 * this.field_21658 - this.field_21654 * this.field_21657;
		float j = this.field_21653 * this.field_21659 - this.field_21655 * this.field_21657;
		float k = this.field_21654 * this.field_21659 - this.field_21655 * this.field_21658;
		float l = this.field_21660 * this.field_21665 - this.field_21661 * this.field_21664;
		float m = this.field_21660 * this.field_21666 - this.field_21662 * this.field_21664;
		float n = this.field_21660 * this.field_21667 - this.field_21663 * this.field_21664;
		float o = this.field_21661 * this.field_21666 - this.field_21662 * this.field_21665;
		float p = this.field_21661 * this.field_21667 - this.field_21663 * this.field_21665;
		float q = this.field_21662 * this.field_21667 - this.field_21663 * this.field_21666;
		float r = this.field_21657 * q - this.field_21658 * p + this.field_21659 * o;
		float s = -this.field_21656 * q + this.field_21658 * n - this.field_21659 * m;
		float t = this.field_21656 * p - this.field_21657 * n + this.field_21659 * l;
		float u = -this.field_21656 * o + this.field_21657 * m - this.field_21658 * l;
		float v = -this.field_21653 * q + this.field_21654 * p - this.field_21655 * o;
		float w = this.field_21652 * q - this.field_21654 * n + this.field_21655 * m;
		float x = -this.field_21652 * p + this.field_21653 * n - this.field_21655 * l;
		float y = this.field_21652 * o - this.field_21653 * m + this.field_21654 * l;
		float z = this.field_21665 * k - this.field_21666 * j + this.field_21667 * i;
		float aa = -this.field_21664 * k + this.field_21666 * h - this.field_21667 * g;
		float ab = this.field_21664 * j - this.field_21665 * h + this.field_21667 * f;
		float ac = -this.field_21664 * i + this.field_21665 * g - this.field_21666 * f;
		float ad = -this.field_21661 * k + this.field_21662 * j - this.field_21663 * i;
		float ae = this.field_21660 * k - this.field_21662 * h + this.field_21663 * g;
		float af = -this.field_21660 * j + this.field_21661 * h - this.field_21663 * f;
		float ag = this.field_21660 * i - this.field_21661 * g + this.field_21662 * f;
		this.field_21652 = r;
		this.field_21656 = s;
		this.field_21660 = t;
		this.field_21664 = u;
		this.field_21653 = v;
		this.field_21657 = w;
		this.field_21661 = x;
		this.field_21665 = y;
		this.field_21654 = z;
		this.field_21658 = aa;
		this.field_21662 = ab;
		this.field_21666 = ac;
		this.field_21655 = ad;
		this.field_21659 = ae;
		this.field_21663 = af;
		this.field_21667 = ag;
		return f * q - g * p + h * o + i * n - j * m + k * l;
	}

	public void transpose() {
		float f = this.field_21656;
		this.field_21656 = this.field_21653;
		this.field_21653 = f;
		f = this.field_21660;
		this.field_21660 = this.field_21654;
		this.field_21654 = f;
		f = this.field_21661;
		this.field_21661 = this.field_21658;
		this.field_21658 = f;
		f = this.field_21664;
		this.field_21664 = this.field_21655;
		this.field_21655 = f;
		f = this.field_21665;
		this.field_21665 = this.field_21659;
		this.field_21659 = f;
		f = this.field_21666;
		this.field_21666 = this.field_21663;
		this.field_21663 = f;
	}

	public boolean invert() {
		float f = this.determinantAndAdjugate();
		if (Math.abs(f) > 1.0E-6F) {
			this.multiply(f);
			return true;
		} else {
			return false;
		}
	}

	public void multiply(Matrix4f matrix) {
		float f = this.field_21652 * matrix.field_21652
			+ this.field_21653 * matrix.field_21656
			+ this.field_21654 * matrix.field_21660
			+ this.field_21655 * matrix.field_21664;
		float g = this.field_21652 * matrix.field_21653
			+ this.field_21653 * matrix.field_21657
			+ this.field_21654 * matrix.field_21661
			+ this.field_21655 * matrix.field_21665;
		float h = this.field_21652 * matrix.field_21654
			+ this.field_21653 * matrix.field_21658
			+ this.field_21654 * matrix.field_21662
			+ this.field_21655 * matrix.field_21666;
		float i = this.field_21652 * matrix.field_21655
			+ this.field_21653 * matrix.field_21659
			+ this.field_21654 * matrix.field_21663
			+ this.field_21655 * matrix.field_21667;
		float j = this.field_21656 * matrix.field_21652
			+ this.field_21657 * matrix.field_21656
			+ this.field_21658 * matrix.field_21660
			+ this.field_21659 * matrix.field_21664;
		float k = this.field_21656 * matrix.field_21653
			+ this.field_21657 * matrix.field_21657
			+ this.field_21658 * matrix.field_21661
			+ this.field_21659 * matrix.field_21665;
		float l = this.field_21656 * matrix.field_21654
			+ this.field_21657 * matrix.field_21658
			+ this.field_21658 * matrix.field_21662
			+ this.field_21659 * matrix.field_21666;
		float m = this.field_21656 * matrix.field_21655
			+ this.field_21657 * matrix.field_21659
			+ this.field_21658 * matrix.field_21663
			+ this.field_21659 * matrix.field_21667;
		float n = this.field_21660 * matrix.field_21652
			+ this.field_21661 * matrix.field_21656
			+ this.field_21662 * matrix.field_21660
			+ this.field_21663 * matrix.field_21664;
		float o = this.field_21660 * matrix.field_21653
			+ this.field_21661 * matrix.field_21657
			+ this.field_21662 * matrix.field_21661
			+ this.field_21663 * matrix.field_21665;
		float p = this.field_21660 * matrix.field_21654
			+ this.field_21661 * matrix.field_21658
			+ this.field_21662 * matrix.field_21662
			+ this.field_21663 * matrix.field_21666;
		float q = this.field_21660 * matrix.field_21655
			+ this.field_21661 * matrix.field_21659
			+ this.field_21662 * matrix.field_21663
			+ this.field_21663 * matrix.field_21667;
		float r = this.field_21664 * matrix.field_21652
			+ this.field_21665 * matrix.field_21656
			+ this.field_21666 * matrix.field_21660
			+ this.field_21667 * matrix.field_21664;
		float s = this.field_21664 * matrix.field_21653
			+ this.field_21665 * matrix.field_21657
			+ this.field_21666 * matrix.field_21661
			+ this.field_21667 * matrix.field_21665;
		float t = this.field_21664 * matrix.field_21654
			+ this.field_21665 * matrix.field_21658
			+ this.field_21666 * matrix.field_21662
			+ this.field_21667 * matrix.field_21666;
		float u = this.field_21664 * matrix.field_21655
			+ this.field_21665 * matrix.field_21659
			+ this.field_21666 * matrix.field_21663
			+ this.field_21667 * matrix.field_21667;
		this.field_21652 = f;
		this.field_21653 = g;
		this.field_21654 = h;
		this.field_21655 = i;
		this.field_21656 = j;
		this.field_21657 = k;
		this.field_21658 = l;
		this.field_21659 = m;
		this.field_21660 = n;
		this.field_21661 = o;
		this.field_21662 = p;
		this.field_21663 = q;
		this.field_21664 = r;
		this.field_21665 = s;
		this.field_21666 = t;
		this.field_21667 = u;
	}

	public void multiply(Quaternion quaternion) {
		this.multiply(new Matrix4f(quaternion));
	}

	public void multiply(float scalar) {
		this.field_21652 *= scalar;
		this.field_21653 *= scalar;
		this.field_21654 *= scalar;
		this.field_21655 *= scalar;
		this.field_21656 *= scalar;
		this.field_21657 *= scalar;
		this.field_21658 *= scalar;
		this.field_21659 *= scalar;
		this.field_21660 *= scalar;
		this.field_21661 *= scalar;
		this.field_21662 *= scalar;
		this.field_21663 *= scalar;
		this.field_21664 *= scalar;
		this.field_21665 *= scalar;
		this.field_21666 *= scalar;
		this.field_21667 *= scalar;
	}

	public static Matrix4f viewboxMatrix(double fov, float aspectRatio, float cameraDepth, float viewDistance) {
		float f = (float)(1.0 / Math.tan(fov * (float) (Math.PI / 180.0) / 2.0));
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.field_21652 = f / aspectRatio;
		matrix4f.field_21657 = f;
		matrix4f.field_21662 = (viewDistance + cameraDepth) / (cameraDepth - viewDistance);
		matrix4f.field_21666 = -1.0F;
		matrix4f.field_21663 = 2.0F * viewDistance * cameraDepth / (cameraDepth - viewDistance);
		return matrix4f;
	}

	public static Matrix4f projectionMatrix(float width, float height, float nearPlane, float farPlane) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.field_21652 = 2.0F / width;
		matrix4f.field_21657 = 2.0F / height;
		float f = farPlane - nearPlane;
		matrix4f.field_21662 = -2.0F / f;
		matrix4f.field_21667 = 1.0F;
		matrix4f.field_21655 = -1.0F;
		matrix4f.field_21659 = -1.0F;
		matrix4f.field_21663 = -(farPlane + nearPlane) / f;
		return matrix4f;
	}

	public void addToLastColumn(Vector3f vector) {
		this.field_21655 = this.field_21655 + vector.getX();
		this.field_21659 = this.field_21659 + vector.getY();
		this.field_21663 = this.field_21663 + vector.getZ();
	}

	public Matrix4f copy() {
		return new Matrix4f(this);
	}

	public static Matrix4f method_24019(float f, float g, float h) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.field_21652 = f;
		matrix4f.field_21657 = g;
		matrix4f.field_21662 = h;
		matrix4f.field_21667 = 1.0F;
		return matrix4f;
	}

	public static Matrix4f method_24021(float f, float g, float h) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.field_21652 = 1.0F;
		matrix4f.field_21657 = 1.0F;
		matrix4f.field_21662 = 1.0F;
		matrix4f.field_21667 = 1.0F;
		matrix4f.field_21655 = f;
		matrix4f.field_21659 = g;
		matrix4f.field_21663 = h;
		return matrix4f;
	}
}
