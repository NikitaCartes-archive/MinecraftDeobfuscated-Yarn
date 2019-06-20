package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

public class class_4117 extends class_4097<class_1314> {
	private final float field_18375;
	private final int field_19352;
	private final int field_19353;

	public class_4117(float f) {
		this(f, 10, 7);
	}

	public class_4117(float f, int i, int j) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18457));
		this.field_18375 = f;
		this.field_19352 = i;
		this.field_19353 = j;
	}

	protected void method_18996(class_3218 arg, class_1314 arg2, long l) {
		class_2338 lv = new class_2338(arg2);
		if (arg.method_19500(lv)) {
			this.method_20429(arg2);
		} else {
			class_4076 lv2 = class_4076.method_18682(lv);
			class_4076 lv3 = class_4215.method_20419(arg, lv2, 2);
			if (lv3 != lv2) {
				this.method_20430(arg2, lv3);
			} else {
				this.method_20429(arg2);
			}
		}
	}

	private void method_20430(class_1314 arg, class_4076 arg2) {
		class_2338 lv = arg2.method_19768();
		Optional<class_243> optional = Optional.ofNullable(
			class_1414.method_6373(
				arg, this.field_19352, this.field_19353, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260())
			)
		);
		arg.method_18868().method_18879(class_4140.field_18445, optional.map(argx -> new class_4142(argx, this.field_18375, 0)));
	}

	private void method_20429(class_1314 arg) {
		Optional<class_243> optional = Optional.ofNullable(class_1414.method_6378(arg, this.field_19352, this.field_19353));
		arg.method_18868().method_18879(class_4140.field_18445, optional.map(argx -> new class_4142(argx, this.field_18375, 0)));
	}
}
