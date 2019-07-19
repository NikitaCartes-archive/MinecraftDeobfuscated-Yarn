package net.minecraft.client.util.math;

import java.nio.FloatBuffer;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public final class Matrix4f {
	private final float[] components = new float[16];

	public Matrix4f() {
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
		this.components[0] = 1.0F - k - l;
		this.components[5] = 1.0F - l - j;
		this.components[10] = 1.0F - j - k;
		this.components[15] = 1.0F;
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.components[1] = 2.0F * (m + r);
		this.components[4] = 2.0F * (m - r);
		this.components[2] = 2.0F * (o - q);
		this.components[8] = 2.0F * (o + q);
		this.components[6] = 2.0F * (n + p);
		this.components[9] = 2.0F * (n - p);
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

	public void setFromBuffer(FloatBuffer buffer) {
		this.setFromBuffer(buffer, false);
	}

	public void setFromBuffer(FloatBuffer buffer, boolean transpose) {
		if (transpose) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.components[i * 4 + j] = buffer.get(j * 4 + i);
				}
			}
		} else {
			buffer.get(this.components);
		}
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
		this.putIntoBuffer(buffer, false);
	}

	public void putIntoBuffer(FloatBuffer buffer, boolean transpose) {
		if (transpose) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					buffer.put(j * 4 + i, this.components[i * 4 + j]);
				}
			}
		} else {
			buffer.put(this.components);
		}
	}

	public void set(int row, int col, float value) {
		this.components[row + 4 * col] = value;
	}

	public static Matrix4f method_4929(double d, float f, float g, float h) {
		float i = (float)(1.0 / Math.tan(d * (float) (Math.PI / 180.0) / 2.0));
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.set(0, 0, i / f);
		matrix4f.set(1, 1, i);
		matrix4f.set(2, 2, (h + g) / (g - h));
		matrix4f.set(3, 2, -1.0F);
		matrix4f.set(2, 3, 2.0F * h * g / (g - h));
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
}
