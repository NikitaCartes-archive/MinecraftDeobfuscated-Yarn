package net.minecraft;

public class class_3727 implements class_3726 {
	private final boolean field_16451;
	private final double field_16450;

	protected class_3727(boolean bl, double d) {
		this.field_16451 = bl;
		this.field_16450 = d;
	}

	public class_3727(class_1297 arg) {
		this(arg.method_5715(), arg.method_5829().field_1322);
	}

	@Override
	public boolean method_16193() {
		return this.field_16451;
	}

	@Override
	public boolean method_16192(class_265 arg, class_2338 arg2) {
		return this.field_16450 > (double)arg2.method_10264() + arg.method_1105(class_2350.class_2351.field_11052) - 1.0E-5F;
	}
}
