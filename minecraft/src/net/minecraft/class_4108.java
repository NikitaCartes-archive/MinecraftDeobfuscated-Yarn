package net.minecraft;

import com.google.common.collect.ImmutableMap;

public class class_4108 extends class_4097<class_1646> {
	private final float field_18362;

	public class_4108(float f) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18458, class_4140.field_18446, class_4141.field_18458), Integer.MAX_VALUE);
		this.field_18362 = f;
	}

	protected boolean method_18954(class_3218 arg, class_1646 arg2) {
		class_1657 lv = arg2.method_8257();
		return arg2.method_5805() && lv != null && !arg2.method_5799() && !arg2.field_6037 && arg2.method_5858(lv) <= 16.0 && lv.field_7512 != null;
	}

	protected boolean method_18955(class_3218 arg, class_1646 arg2, long l) {
		return this.method_18954(arg, arg2);
	}

	protected void method_18956(class_3218 arg, class_1646 arg2, long l) {
		this.method_18953(arg2);
	}

	protected void method_18957(class_3218 arg, class_1646 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		lv.method_18875(class_4140.field_18445);
		lv.method_18875(class_4140.field_18446);
	}

	protected void method_18958(class_3218 arg, class_1646 arg2, long l) {
		this.method_18953(arg2);
	}

	@Override
	protected boolean method_18915(long l) {
		return false;
	}

	private void method_18953(class_1646 arg) {
		class_4102 lv = new class_4102(arg.method_8257());
		class_4095<?> lv2 = arg.method_18868();
		lv2.method_18878(class_4140.field_18445, new class_4142(lv, this.field_18362, 2));
		lv2.method_18878(class_4140.field_18446, lv);
	}
}
