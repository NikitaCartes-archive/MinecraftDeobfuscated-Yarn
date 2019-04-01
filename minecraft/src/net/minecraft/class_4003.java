package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_4003 extends class_3940 {
	protected class_1058 field_17886;

	protected class_4003(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f);
	}

	protected class_4003(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, g, h, i);
	}

	protected void method_18141(class_1058 arg) {
		this.field_17886 = arg;
	}

	@Override
	protected float method_18133() {
		return this.field_17886.method_4594();
	}

	@Override
	protected float method_18134() {
		return this.field_17886.method_4577();
	}

	@Override
	protected float method_18135() {
		return this.field_17886.method_4593();
	}

	@Override
	protected float method_18136() {
		return this.field_17886.method_4575();
	}

	public void method_18140(class_4002 arg) {
		this.method_18141(arg.method_18139(this.field_3840));
	}

	public void method_18142(class_4002 arg) {
		this.method_18141(arg.method_18138(this.field_3866, this.field_3847));
	}
}
