package net.minecraft;

import com.google.common.collect.ImmutableMap;

public class class_4250 extends class_4097<class_1309> {
	public class_4250() {
		super(ImmutableMap.of());
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		return arg.field_9229.nextInt(20) == 0;
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		class_3765 lv2 = arg.method_19502(new class_2338(arg2));
		if (lv2 == null || lv2.method_20022() || lv2.method_20024()) {
			lv.method_18897(class_4168.field_18595);
			lv.method_18871(arg.method_8532(), arg.method_8510());
		}
	}
}
