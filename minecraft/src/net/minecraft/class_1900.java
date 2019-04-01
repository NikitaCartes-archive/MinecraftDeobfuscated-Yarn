package net.minecraft;

public class class_1900 extends class_1887 {
	public final class_1900.class_1901 field_9133;

	public class_1900(class_1887.class_1888 arg, class_1900.class_1901 arg2, class_1304... args) {
		super(arg, class_1886.field_9068, args);
		this.field_9133 = arg2;
		if (arg2 == class_1900.class_1901.field_9140) {
			this.field_9083 = class_1886.field_9079;
		}
	}

	@Override
	public int method_8182(int i) {
		return this.field_9133.method_8240() + (i - 1) * this.field_9133.method_8239();
	}

	@Override
	public int method_8183() {
		return 4;
	}

	@Override
	public int method_8181(int i, class_1282 arg) {
		if (arg.method_5538()) {
			return 0;
		} else if (this.field_9133 == class_1900.class_1901.field_9138) {
			return i;
		} else if (this.field_9133 == class_1900.class_1901.field_9139 && arg.method_5534()) {
			return i * 2;
		} else if (this.field_9133 == class_1900.class_1901.field_9140 && arg == class_1282.field_5868) {
			return i * 3;
		} else if (this.field_9133 == class_1900.class_1901.field_9141 && arg.method_5535()) {
			return i * 2;
		} else {
			return this.field_9133 == class_1900.class_1901.field_9142 && arg.method_5533() ? i * 2 : 0;
		}
	}

	@Override
	public boolean method_8180(class_1887 arg) {
		if (arg instanceof class_1900) {
			class_1900 lv = (class_1900)arg;
			return this.field_9133 != lv.field_9133;
		} else {
			return super.method_8180(arg);
		}
	}

	public static int method_8238(class_1309 arg, int i) {
		int j = class_1890.method_8203(class_1893.field_9095, arg);
		if (j > 0) {
			i -= class_3532.method_15375((float)i * (float)j * 0.15F);
		}

		return i;
	}

	public static double method_8237(class_1309 arg, double d) {
		int i = class_1890.method_8203(class_1893.field_9107, arg);
		if (i > 0) {
			d -= (double)class_3532.method_15357(d * (double)((float)i * 0.15F));
		}

		return d;
	}

	public static enum class_1901 {
		field_9138("all", 1, 11),
		field_9139("fire", 10, 8),
		field_9140("fall", 5, 6),
		field_9141("explosion", 5, 8),
		field_9142("projectile", 3, 6);

		private final String field_9137;
		private final int field_9135;
		private final int field_9134;

		private class_1901(String string2, int j, int k) {
			this.field_9137 = string2;
			this.field_9135 = j;
			this.field_9134 = k;
		}

		public int method_8240() {
			return this.field_9135;
		}

		public int method_8239() {
			return this.field_9134;
		}
	}
}
