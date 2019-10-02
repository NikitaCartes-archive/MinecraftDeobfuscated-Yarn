package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public class Frustum {
	private final Vector4f[] field_20994 = new Vector4f[6];
	private double field_20995;
	private double field_20996;
	private double field_20997;

	public Frustum(Matrix4f matrix4f, Matrix4f matrix4f2) {
		this.method_23092(matrix4f, matrix4f2);
	}

	public void method_23088(double d, double e, double f) {
		this.field_20995 = d;
		this.field_20996 = e;
		this.field_20997 = f;
	}

	private void method_23092(Matrix4f matrix4f, Matrix4f matrix4f2) {
		Matrix4f matrix4f3 = new Matrix4f(matrix4f2);
		matrix4f3.multiply(matrix4f);
		matrix4f3.transpose();
		this.method_23091(matrix4f3, -1, 0, 0, 0);
		this.method_23091(matrix4f3, 1, 0, 0, 1);
		this.method_23091(matrix4f3, 0, -1, 0, 2);
		this.method_23091(matrix4f3, 0, 1, 0, 3);
		this.method_23091(matrix4f3, 0, 0, -1, 4);
		this.method_23091(matrix4f3, 0, 0, 1, 5);
	}

	private void method_23091(Matrix4f matrix4f, int i, int j, int k, int l) {
		Vector4f vector4f = new Vector4f((float)i, (float)j, (float)k, 1.0F);
		vector4f.multiply(matrix4f);
		vector4f.method_23218();
		this.field_20994[l] = vector4f;
	}

	public boolean method_23093(Box box) {
		return this.method_23089(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	private boolean method_23089(double d, double e, double f, double g, double h, double i) {
		float j = (float)(d - this.field_20995);
		float k = (float)(e - this.field_20996);
		float l = (float)(f - this.field_20997);
		float m = (float)(g - this.field_20995);
		float n = (float)(h - this.field_20996);
		float o = (float)(i - this.field_20997);
		return this.method_23090(j, k, l, m, n, o);
	}

	private boolean method_23090(float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < 6; l++) {
			Vector4f vector4f = this.field_20994[l];
			if (!(vector4f.method_23217(new Vector4f(f, g, h, 1.0F)) > 0.0F)
				&& !(vector4f.method_23217(new Vector4f(i, g, h, 1.0F)) > 0.0F)
				&& !(vector4f.method_23217(new Vector4f(f, j, h, 1.0F)) > 0.0F)
				&& !(vector4f.method_23217(new Vector4f(i, j, h, 1.0F)) > 0.0F)
				&& !(vector4f.method_23217(new Vector4f(f, g, k, 1.0F)) > 0.0F)
				&& !(vector4f.method_23217(new Vector4f(i, g, k, 1.0F)) > 0.0F)
				&& !(vector4f.method_23217(new Vector4f(f, j, k, 1.0F)) > 0.0F)
				&& !(vector4f.method_23217(new Vector4f(i, j, k, 1.0F)) > 0.0F)) {
				return false;
			}
		}

		return true;
	}
}
