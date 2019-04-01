package net.minecraft;

import java.util.EnumSet;

public class class_1342 extends class_1352 {
	private final class_1314 field_6408;

	public class_1342(class_1314 arg) {
		this.field_6408 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
	}

	@Override
	public boolean method_6264() {
		return this.field_6408.method_5669() < 140;
	}

	@Override
	public boolean method_6266() {
		return this.method_6264();
	}

	@Override
	public boolean method_6267() {
		return false;
	}

	@Override
	public void method_6269() {
		this.method_6252();
	}

	private void method_6252() {
		Iterable<class_2338> iterable = class_2338.method_10094(
			class_3532.method_15357(this.field_6408.field_5987 - 1.0),
			class_3532.method_15357(this.field_6408.field_6010),
			class_3532.method_15357(this.field_6408.field_6035 - 1.0),
			class_3532.method_15357(this.field_6408.field_5987 + 1.0),
			class_3532.method_15357(this.field_6408.field_6010 + 8.0),
			class_3532.method_15357(this.field_6408.field_6035 + 1.0)
		);
		class_2338 lv = null;

		for (class_2338 lv2 : iterable) {
			if (this.method_6253(this.field_6408.field_6002, lv2)) {
				lv = lv2;
				break;
			}
		}

		if (lv == null) {
			lv = new class_2338(this.field_6408.field_5987, this.field_6408.field_6010 + 8.0, this.field_6408.field_6035);
		}

		this.field_6408.method_5942().method_6337((double)lv.method_10263(), (double)(lv.method_10264() + 1), (double)lv.method_10260(), 1.0);
	}

	@Override
	public void method_6268() {
		this.method_6252();
		this.field_6408.method_5724(0.02F, new class_243((double)this.field_6408.field_6212, (double)this.field_6408.field_6227, (double)this.field_6408.field_6250));
		this.field_6408.method_5784(class_1313.field_6308, this.field_6408.method_18798());
	}

	private boolean method_6253(class_1941 arg, class_2338 arg2) {
		class_2680 lv = arg.method_8320(arg2);
		return (arg.method_8316(arg2).method_15769() || lv.method_11614() == class_2246.field_10422) && lv.method_11609(arg, arg2, class_10.field_50);
	}
}
