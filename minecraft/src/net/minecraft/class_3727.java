package net.minecraft;

public class class_3727 implements class_3726 {
	protected static final class_3726 field_17593 = new class_3727(false, -Double.MAX_VALUE, class_1802.field_8162) {
		@Override
		public boolean method_16192(class_265 arg, class_2338 arg2, boolean bl) {
			return bl;
		}
	};
	private final boolean field_16451;
	private final double field_16450;
	private final class_1792 field_17594;

	protected class_3727(boolean bl, double d, class_1792 arg) {
		this.field_16451 = bl;
		this.field_16450 = d;
		this.field_17594 = arg;
	}

	@Deprecated
	protected class_3727(class_1297 arg) {
		boolean var10001 = arg.method_5715();
		class_1792 var10003 = arg instanceof class_1309 ? ((class_1309)arg).method_6047().method_7909() : class_1802.field_8162;
		this(var10001, arg.method_5829().field_1322, var10003);
	}

	@Override
	public boolean method_17785(class_1792 arg) {
		return this.field_17594 == arg;
	}

	@Override
	public boolean method_16193() {
		return this.field_16451;
	}

	@Override
	public boolean method_16192(class_265 arg, class_2338 arg2, boolean bl) {
		return this.field_16450 > (double)arg2.method_10264() + arg.method_1105(class_2350.class_2351.field_11052) - 1.0E-5F;
	}
}
