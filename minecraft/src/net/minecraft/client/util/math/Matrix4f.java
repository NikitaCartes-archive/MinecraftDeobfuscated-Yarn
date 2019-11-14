package net.minecraft.client.util.math;

import java.nio.FloatBuffer;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public final class Matrix4f {
	private final float[] components;

	public Matrix4f() {
		this(new float[16]);
	}

	public Matrix4f(float[] components) {
		this.components = components;
	}

	public Matrix4f(Quaternion quaternion) {
		this();
		float f = quaternion.getB();
		float g = quaternion.getC();
		float h = quaternion.getD();
		float i = quaternion.getA();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.set(0, 0, 1.0F - k - l);
		this.set(1, 1, 1.0F - l - j);
		this.set(2, 2, 1.0F - j - k);
		this.set(3, 3, 1.0F);
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.set(1, 0, 2.0F * (m + r));
		this.set(0, 1, 2.0F * (m - r));
		this.set(2, 0, 2.0F * (o - q));
		this.set(0, 2, 2.0F * (o + q));
		this.set(2, 1, 2.0F * (n + p));
		this.set(1, 2, 2.0F * (n - p));
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Matrix4f matrix4f = (Matrix4f)o;
			return Arrays.equals(this.components, matrix4f.components);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.components);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix4f:\n");

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				stringBuilder.append(this.components[i + j * 4]);
				if (j != 3) {
					stringBuilder.append(" ");
				}
			}

			stringBuilder.append("\n");
		}

		return stringBuilder.toString();
	}

	public void writeToBuffer(FloatBuffer buffer) {
		this.writeToBuffer(buffer, false);
	}

	public void writeToBuffer(FloatBuffer buffer, boolean atBufferStart) {
		if (atBufferStart) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					buffer.put(j * 4 + i, this.components[i * 4 + j]);
				}
			}
		} else {
			buffer.put(this.components);
		}
	}

	public void loadIdentity() {
		this.components[0] = 1.0F;
		this.components[1] = 0.0F;
		this.components[2] = 0.0F;
		this.components[3] = 0.0F;
		this.components[4] = 0.0F;
		this.components[5] = 1.0F;
		this.components[6] = 0.0F;
		this.components[7] = 0.0F;
		this.components[8] = 0.0F;
		this.components[9] = 0.0F;
		this.components[10] = 1.0F;
		this.components[11] = 0.0F;
		this.components[12] = 0.0F;
		this.components[13] = 0.0F;
		this.components[14] = 0.0F;
		this.components[15] = 1.0F;
	}

	public float get(int row, int column) {
		return this.components[4 * column + row];
	}

	public void set(int row, int column, float value) {
		this.components[4 * column + row] = value;
	}

	public float determinantAndAdjugate() {
		float f = this.minor(0, 1, 0, 1);
		float g = this.minor(0, 1, 0, 2);
		float h = this.minor(0, 1, 0, 3);
		float i = this.minor(0, 1, 1, 2);
		float j = this.minor(0, 1, 1, 3);
		float k = this.minor(0, 1, 2, 3);
		float l = this.minor(2, 3, 0, 1);
		float m = this.minor(2, 3, 0, 2);
		float n = this.minor(2, 3, 0, 3);
		float o = this.minor(2, 3, 1, 2);
		float p = this.minor(2, 3, 1, 3);
		float q = this.minor(2, 3, 2, 3);
		float r = this.get(1, 1) * q - this.get(1, 2) * p + this.get(1, 3) * o;
		float s = -this.get(1, 0) * q + this.get(1, 2) * n - this.get(1, 3) * m;
		float t = this.get(1, 0) * p - this.get(1, 1) * n + this.get(1, 3) * l;
		float u = -this.get(1, 0) * o + this.get(1, 1) * m - this.get(1, 2) * l;
		float v = -this.get(0, 1) * q + this.get(0, 2) * p - this.get(0, 3) * o;
		float w = this.get(0, 0) * q - this.get(0, 2) * n + this.get(0, 3) * m;
		float x = -this.get(0, 0) * p + this.get(0, 1) * n - this.get(0, 3) * l;
		float y = this.get(0, 0) * o - this.get(0, 1) * m + this.get(0, 2) * l;
		float z = this.get(3, 1) * k - this.get(3, 2) * j + this.get(3, 3) * i;
		float aa = -this.get(3, 0) * k + this.get(3, 2) * h - this.get(3, 3) * g;
		float ab = this.get(3, 0) * j - this.get(3, 1) * h + this.get(3, 3) * f;
		float ac = -this.get(3, 0) * i + this.get(3, 1) * g - this.get(3, 2) * f;
		float ad = -this.get(2, 1) * k + this.get(2, 2) * j - this.get(2, 3) * i;
		float ae = this.get(2, 0) * k - this.get(2, 2) * h + this.get(2, 3) * g;
		float af = -this.get(2, 0) * j + this.get(2, 1) * h - this.get(2, 3) * f;
		float ag = this.get(2, 0) * i - this.get(2, 1) * g + this.get(2, 2) * f;
		this.set(0, 0, r);
		this.set(1, 0, s);
		this.set(2, 0, t);
		this.set(3, 0, u);
		this.set(0, 1, v);
		this.set(1, 1, w);
		this.set(2, 1, x);
		this.set(3, 1, y);
		this.set(0, 2, z);
		this.set(1, 2, aa);
		this.set(2, 2, ab);
		this.set(3, 2, ac);
		this.set(0, 3, ad);
		this.set(1, 3, ae);
		this.set(2, 3, af);
		this.set(3, 3, ag);
		return f * q - g * p + h * o + i * n - j * m + k * l;
	}

	public void transpose() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < i; j++) {
				this.transpose(i, j);
			}
		}
	}

	private void transpose(int row, int column) {
		float f = this.components[row + column * 4];
		this.components[row + column * 4] = this.components[column + row * 4];
		this.components[column + row * 4] = f;
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

	private float minor(int row1, int row2, int column1, int column2) {
		return this.get(row1, column1) * this.get(row2, column2) - this.get(row1, column2) * this.get(row2, column1);
	}

	public void multiply(Matrix4f matrix) {
		float[] fs = Arrays.copyOf(this.components, 16);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.components[i + j * 4] = 0.0F;

				for (int k = 0; k < 4; k++) {
					this.components[i + j * 4] = this.components[i + j * 4] + fs[i + k * 4] * matrix.components[k + j * 4];
				}
			}
		}
	}

	public void multiply(Quaternion quaternion) {
		this.multiply(new Matrix4f(quaternion));
	}

	public void multiply(float scalar) {
		for (int i = 0; i < 16; i++) {
			this.components[i] = this.components[i] * scalar;
		}
	}

	public static Matrix4f viewboxMatrix(double fov, float aspectRatio, float cameraDepth, float viewDistance) {
		float f = (float)(1.0 / Math.tan(fov * (float) (Math.PI / 180.0) / 2.0));
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.set(0, 0, f / aspectRatio);
		matrix4f.set(1, 1, f);
		matrix4f.set(2, 2, (viewDistance + cameraDepth) / (cameraDepth - viewDistance));
		matrix4f.set(3, 2, -1.0F);
		matrix4f.set(2, 3, 2.0F * viewDistance * cameraDepth / (cameraDepth - viewDistance));
		return matrix4f;
	}

	public static Matrix4f projectionMatrix(float width, float height, float nearPlane, float farPlane) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.set(0, 0, 2.0F / width);
		matrix4f.set(1, 1, 2.0F / height);
		float f = farPlane - nearPlane;
		matrix4f.set(2, 2, -2.0F / f);
		matrix4f.set(3, 3, 1.0F);
		matrix4f.set(0, 3, -1.0F);
		matrix4f.set(1, 3, -1.0F);
		matrix4f.set(2, 3, -(farPlane + nearPlane) / f);
		return matrix4f;
	}

	public void addToLastColumn(Vector3f vector) {
		this.set(0, 3, this.get(0, 3) + vector.getX());
		this.set(1, 3, this.get(1, 3) + vector.getY());
		this.set(2, 3, this.get(2, 3) + vector.getZ());
	}

	public Matrix4f copy() {
		return new Matrix4f((float[])this.components.clone());
	}
}
