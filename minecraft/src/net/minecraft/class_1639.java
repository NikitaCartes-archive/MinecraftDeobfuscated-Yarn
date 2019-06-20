package net.minecraft;

import javax.annotation.Nullable;

public class class_1639 extends class_1547 {
	public class_1639(class_1299<? extends class_1639> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5941(class_7.field_14, 8.0F);
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15214;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15027;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15122;
	}

	@Override
	class_3414 method_6998() {
		return class_3417.field_14955;
	}

	@Override
	protected void method_6099(class_1282 arg, int i, boolean bl) {
		super.method_6099(arg, i, bl);
		class_1297 lv = arg.method_5529();
		if (lv instanceof class_1548) {
			class_1548 lv2 = (class_1548)lv;
			if (lv2.method_7008()) {
				lv2.method_7002();
				this.method_5706(class_1802.field_8791);
			}
		}
	}

	@Override
	protected void method_5964(class_1266 arg) {
		this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8528));
	}

	@Override
	protected void method_5984(class_1266 arg) {
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		class_1315 lv = super.method_5943(arg, arg2, arg3, arg4, arg5);
		this.method_5996(class_1612.field_7363).method_6192(4.0);
		this.method_6997();
		return lv;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 2.1F;
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		if (!super.method_6121(arg)) {
			return false;
		} else {
			if (arg instanceof class_1309) {
				((class_1309)arg).method_6092(new class_1293(class_1294.field_5920, 200));
			}

			return true;
		}
	}

	@Override
	protected class_1665 method_6996(class_1799 arg, float f) {
		class_1665 lv = super.method_6996(arg, f);
		lv.method_5639(100);
		return lv;
	}

	@Override
	public boolean method_6049(class_1293 arg) {
		return arg.method_5579() == class_1294.field_5920 ? false : super.method_6049(arg);
	}
}
