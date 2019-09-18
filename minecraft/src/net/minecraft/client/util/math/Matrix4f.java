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
		this.components = new float[16];
	}

	public Matrix4f(Quaternion quaternion) {
		this();
		float f = quaternion.getX();
		float g = quaternion.getY();
		float h = quaternion.getZ();
		float i = quaternion.getW();
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

	public Matrix4f(float[] fs) {
		this(fs, false);
	}

	public Matrix4f(float[] fs, boolean bl) {
		if (bl) {
			this.components = new float[16];

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.components[i * 4 + j] = fs[j * 4 + i];
				}
			}
		} else {
			this.components = Arrays.copyOf(fs, fs.length);
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Matrix4f matrix4f = (Matrix4f)object;
			return Arrays.equals(this.components, matrix4f.components);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.components);
	}

	public void setFromBuffer(FloatBuffer floatBuffer) {
		this.setFromBuffer(floatBuffer, false);
	}

	public void setFromBuffer(FloatBuffer floatBuffer, boolean bl) {
		if (bl) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.components[i * 4 + j] = floatBuffer.get(j * 4 + i);
				}
			}
		} else {
			floatBuffer.get(this.components);
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

	public void putIntoBuffer(FloatBuffer floatBuffer) {
		this.putIntoBuffer(floatBuffer, false);
	}

	public void putIntoBuffer(FloatBuffer floatBuffer, boolean bl) {
		if (bl) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					floatBuffer.put(j * 4 + i, this.components[i * 4 + j]);
				}
			}
		} else {
			floatBuffer.put(this.components);
		}
	}

	public void method_22668() {
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

	public float method_22669(int i, int j) {
		return this.components[i + 4 * j];
	}

	public void set(int i, int j, float f) {
		this.components[i + 4 * j] = f;
	}

	public void method_22672(Matrix4f matrix4f) {
		float[] fs = Arrays.copyOf(this.components, 16);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.components[i + j * 4] = 0.0F;

				for (int k = 0; k < 4; k++) {
					this.components[i + j * 4] = this.components[i + j * 4] + fs[i + k * 4] * matrix4f.components[k + j * 4];
				}
			}
		}
	}

	public void method_22670(Quaternion quaternion) {
		this.method_22672(new Matrix4f(quaternion));
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

	public static Matrix4f projectionMatrix(float f, float g, float h, float i) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.set(0, 0, 2.0F / f);
		matrix4f.set(1, 1, 2.0F / g);
		float j = i - h;
		matrix4f.set(2, 2, -2.0F / j);
		matrix4f.set(3, 3, 1.0F);
		matrix4f.set(0, 3, -1.0F);
		matrix4f.set(1, 3, -1.0F);
		matrix4f.set(2, 3, -(i + h) / j);
		return matrix4f;
	}

	public void method_22671(Vector3f vector3f) {
		this.set(0, 3, this.method_22669(0, 3) + vector3f.getX());
		this.set(1, 3, this.method_22669(1, 3) + vector3f.getY());
		this.set(2, 3, this.method_22669(2, 3) + vector3f.getZ());
	}

	public Matrix4f method_22673() {
		return new Matrix4f((float[])this.components.clone());
	}
}
