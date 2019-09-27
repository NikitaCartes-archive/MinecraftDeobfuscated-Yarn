package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_4583 extends class_4585 {
	private final class_4588 field_20866;
	private final double field_20867;
	private final double field_20868;
	private final double field_20869;
	private double field_20870;
	private double field_20871;
	private double field_20872;
	private int field_20873;
	private int field_20874;
	private int field_20875;
	private int field_20876;
	private int field_20877;
	private int field_20878;
	private int field_20879;
	private float field_20880;
	private float field_20881;
	private float field_20882;

	public class_4583(class_4588 arg, double d, double e, double f) {
		this.field_20866 = arg;
		this.field_20867 = d;
		this.field_20868 = e;
		this.field_20869 = f;
		this.method_22891();
	}

	private void method_22891() {
		this.field_20870 = 0.0;
		this.field_20871 = 0.0;
		this.field_20872 = 0.0;
		this.field_20873 = this.field_20890;
		this.field_20874 = this.field_20891;
		this.field_20875 = this.field_20892;
		this.field_20876 = this.field_20893;
		this.field_20877 = this.field_20895;
		this.field_20878 = this.field_20896;
		this.field_20879 = 15728880;
		this.field_20880 = 0.0F;
		this.field_20881 = 1.0F;
		this.field_20882 = 0.0F;
	}

	@Override
	public void next() {
		Direction direction = Direction.getFacing(this.field_20880, this.field_20881, this.field_20882);
		double d = this.field_20870 + this.field_20867;
		double e = this.field_20871 + this.field_20868;
		double f = this.field_20872 + this.field_20869;
		double g;
		double h;
		switch (direction.getAxis()) {
			case X:
				g = f;
				h = e;
				break;
			case Y:
				g = d;
				h = f;
				break;
			case Z:
			default:
				g = d;
				h = e;
		}

		float i = (float)(MathHelper.fractionalPart(g / 256.0) * 256.0);
		float j = (float)(MathHelper.fractionalPart(h / 256.0) * 256.0);
		this.field_20866
			.vertex(this.field_20870, this.field_20871, this.field_20872)
			.color(this.field_20873, this.field_20874, this.field_20875, this.field_20876)
			.texture(i, j)
			.method_22917(this.field_20877, this.field_20878)
			.method_22916(this.field_20879)
			.method_22914(this.field_20880, this.field_20881, this.field_20882)
			.next();
		this.method_22891();
	}

	@Override
	public class_4588 vertex(double d, double e, double f) {
		this.field_20870 = d;
		this.field_20871 = e;
		this.field_20872 = f;
		return this;
	}

	@Override
	public class_4588 color(int i, int j, int k, int l) {
		if (this.field_20889) {
			throw new IllegalStateException();
		} else {
			this.field_20873 = i;
			this.field_20874 = j;
			this.field_20875 = k;
			this.field_20876 = l;
			return this;
		}
	}

	@Override
	public class_4588 texture(float f, float g) {
		return this;
	}

	@Override
	public class_4588 method_22917(int i, int j) {
		if (this.field_20894) {
			throw new IllegalStateException();
		} else {
			this.field_20877 = i;
			this.field_20878 = j;
			return this;
		}
	}

	@Override
	public class_4588 method_22921(int i, int j) {
		this.field_20879 = i | j << 16;
		return this;
	}

	@Override
	public class_4588 method_22914(float f, float g, float h) {
		this.field_20880 = f;
		this.field_20881 = g;
		this.field_20882 = h;
		return this;
	}
}
