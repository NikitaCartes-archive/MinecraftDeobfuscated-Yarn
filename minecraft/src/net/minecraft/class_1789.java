package net.minecraft;

public class class_1789 extends class_1792 {
	private final int field_7997;
	private final float field_7993;
	private final boolean field_7996;
	private boolean field_7995;
	private boolean field_7994;
	private class_1293 field_7992;
	private float field_7998;

	public class_1789(int i, float f, boolean bl, class_1792.class_1793 arg) {
		super(arg);
		this.field_7997 = i;
		this.field_7996 = bl;
		this.field_7993 = f;
	}

	@Override
	public class_1799 method_7861(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		if (arg3 instanceof class_1657) {
			class_1657 lv = (class_1657)arg3;
			lv.method_7344().method_7579(this, arg);
			arg2.method_8465(
				null, lv.field_5987, lv.field_6010, lv.field_6035, class_3417.field_14990, class_3419.field_15248, 0.5F, arg2.field_9229.nextFloat() * 0.1F + 0.9F
			);
			this.method_7831(arg, arg2, lv);
			lv.method_7259(class_3468.field_15372.method_14956(this));
			if (lv instanceof class_3222) {
				class_174.field_1198.method_8821((class_3222)lv, arg);
			}
		}

		arg.method_7934(1);
		return arg;
	}

	protected void method_7831(class_1799 arg, class_1937 arg2, class_1657 arg3) {
		if (!arg2.field_9236 && this.field_7992 != null && arg2.field_9229.nextFloat() < this.field_7998) {
			arg3.method_6092(new class_1293(this.field_7992));
		}
	}

	@Override
	public int method_7881(class_1799 arg) {
		return this.field_7994 ? 16 : 32;
	}

	@Override
	public class_1839 method_7853(class_1799 arg) {
		return class_1839.field_8950;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		if (arg2.method_7332(this.field_7995)) {
			arg2.method_6019(arg3);
			return new class_1271<>(class_1269.field_5812, lv);
		} else {
			return new class_1271<>(class_1269.field_5814, lv);
		}
	}

	public int method_7832(class_1799 arg) {
		return this.field_7997;
	}

	public float method_7827(class_1799 arg) {
		return this.field_7993;
	}

	public boolean method_7833() {
		return this.field_7996;
	}

	public class_1789 method_7830(class_1293 arg, float f) {
		this.field_7992 = arg;
		this.field_7998 = f;
		return this;
	}

	public class_1789 method_7828() {
		this.field_7995 = true;
		return this;
	}

	public class_1789 method_7829() {
		this.field_7994 = true;
		return this;
	}
}
