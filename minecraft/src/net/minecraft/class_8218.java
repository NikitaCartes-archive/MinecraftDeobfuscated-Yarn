package net.minecraft;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

public record class_8218(float sinHalf, float cosHalf) {
	public static class_8218 method_49727(float f, float g) {
		float h = Math.invsqrt(f * f + g * g);
		return new class_8218(h * f, h * g);
	}

	public static class_8218 method_49726(float f) {
		float g = Math.sin(f / 2.0F);
		float h = Math.cosFromSin(g, f / 2.0F);
		return new class_8218(g, h);
	}

	public class_8218 method_49725() {
		return new class_8218(-this.sinHalf, this.cosHalf);
	}

	public Quaternionf method_49729(Quaternionf quaternionf) {
		return quaternionf.set(this.sinHalf, 0.0F, 0.0F, this.cosHalf);
	}

	public Quaternionf method_49732(Quaternionf quaternionf) {
		return quaternionf.set(0.0F, this.sinHalf, 0.0F, this.cosHalf);
	}

	public Quaternionf method_49735(Quaternionf quaternionf) {
		return quaternionf.set(0.0F, 0.0F, this.sinHalf, this.cosHalf);
	}

	public float method_49730() {
		return this.cosHalf * this.cosHalf - this.sinHalf * this.sinHalf;
	}

	public float method_49733() {
		return 2.0F * this.sinHalf * this.cosHalf;
	}

	public Matrix3f method_49728(Matrix3f matrix3f) {
		matrix3f.m01 = 0.0F;
		matrix3f.m02 = 0.0F;
		matrix3f.m10 = 0.0F;
		matrix3f.m20 = 0.0F;
		float f = this.method_49730();
		float g = this.method_49733();
		matrix3f.m11 = f;
		matrix3f.m22 = f;
		matrix3f.m12 = g;
		matrix3f.m21 = -g;
		matrix3f.m00 = 1.0F;
		return matrix3f;
	}

	public Matrix3f method_49731(Matrix3f matrix3f) {
		matrix3f.m01 = 0.0F;
		matrix3f.m10 = 0.0F;
		matrix3f.m12 = 0.0F;
		matrix3f.m21 = 0.0F;
		float f = this.method_49730();
		float g = this.method_49733();
		matrix3f.m00 = f;
		matrix3f.m22 = f;
		matrix3f.m02 = -g;
		matrix3f.m20 = g;
		matrix3f.m11 = 1.0F;
		return matrix3f;
	}

	public Matrix3f method_49734(Matrix3f matrix3f) {
		matrix3f.m02 = 0.0F;
		matrix3f.m12 = 0.0F;
		matrix3f.m20 = 0.0F;
		matrix3f.m21 = 0.0F;
		float f = this.method_49730();
		float g = this.method_49733();
		matrix3f.m00 = f;
		matrix3f.m11 = f;
		matrix3f.m01 = g;
		matrix3f.m10 = -g;
		matrix3f.m22 = 1.0F;
		return matrix3f;
	}
}
