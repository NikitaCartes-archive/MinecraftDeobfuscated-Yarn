package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4586 extends class_4585 {
	private final class_4588 field_20897;

	public class_4586(class_4588 arg) {
		this.field_20897 = arg;
	}

	public void method_22902(int i, int j, int k, int l) {
		super.method_22901(i, j, k, l);
	}

	@Override
	public void method_22901(int i, int j, int k, int l) {
	}

	@Override
	public void method_22922(int i, int j) {
	}

	@Override
	public void method_22923() {
	}

	@Override
	public class_4588 vertex(double d, double e, double f) {
		this.field_20897.vertex(d, e, f).color(this.field_20890, this.field_20891, this.field_20892, this.field_20893).next();
		return this;
	}

	@Override
	public class_4588 color(int i, int j, int k, int l) {
		return this;
	}

	@Override
	public class_4588 texture(float f, float g) {
		return this;
	}

	@Override
	public class_4588 method_22917(int i, int j) {
		return this;
	}

	@Override
	public class_4588 method_22921(int i, int j) {
		return this;
	}

	@Override
	public class_4588 method_22914(float f, float g, float h) {
		return this;
	}

	@Override
	public void next() {
	}
}
