package net.minecraft.client.util.math;

import java.nio.FloatBuffer;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Matrix4f {
	private final float[] components;

	public Matrix4f() {
		this.components = new float[16];
	}

	public Matrix4f(Quaternion quaternion) {
		this();
		float f = quaternion.method_4921();
		float g = quaternion.method_4922();
		float h = quaternion.method_4923();
		float i = quaternion.method_4924();
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

	public Matrix4f(Matrix4f matrix4f) {
		this.components = Arrays.copyOf(matrix4f.components, 16);
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

	public void setIdentity() {
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

	public float get(int i, int j) {
		return this.components[i + 4 * j];
	}

	public void set(int i, int j, float f) {
		this.components[i + 4 * j] = f;
	}

	public void multiply(Matrix4f matrix4f) {
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

	public void multiply(float f) {
		for (int i = 0; i < 16; i++) {
			this.components[i] = this.components[i] * f;
		}
	}

	public void add(Matrix4f matrix4f) {
		for (int i = 0; i < 16; i++) {
			this.components[i] = this.components[i] + matrix4f.components[i];
		}
	}

	public void subtract(Matrix4f matrix4f) {
		for (int i = 0; i < 16; i++) {
			this.components[i] = this.components[i] - matrix4f.components[i];
		}
	}

	public float method_4934() {
		float f = 0.0F;

		for (int i = 0; i < 4; i++) {
			f += this.components[i + 4 * i];
		}

		return f;
	}

	public void method_4941() {
		Matrix4f matrix4f = new Matrix4f();
		Matrix4f matrix4f2 = new Matrix4f(this);
		Matrix4f matrix4f3 = new Matrix4f(this);
		matrix4f2.multiply(this);
		matrix4f3.multiply(matrix4f2);
		float f = this.method_4934();
		float g = matrix4f2.method_4934();
		float h = matrix4f3.method_4934();
		this.multiply((g - f * f) / 2.0F);
		matrix4f.setIdentity();
		matrix4f.multiply((f * f * f - 3.0F * f * g + 2.0F * h) / 6.0F);
		this.add(matrix4f);
		matrix4f2.multiply(f);
		this.add(matrix4f2);
		this.subtract(matrix4f3);
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

	public static Matrix4f method_4933(float f, float g, float h, float i) {
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
}
