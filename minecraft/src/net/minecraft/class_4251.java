package net.minecraft;

import com.google.common.collect.ImmutableMap;

public class class_4251 extends class_4097<class_1309> {
	public class_4251() {
		super(ImmutableMap.of(class_4140.field_18440, class_4141.field_18456));
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		return arg.field_9229.nextFloat() > 0.95F;
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		class_2338 lv2 = ((class_4208)lv.method_18904(class_4140.field_18440).get()).method_19446();
		if (lv2.method_19771(new class_2338(arg2), 3.0)) {
			class_2680 lv3 = arg.method_8320(lv2);
			if (lv3.method_11614() == class_2246.field_16332) {
				class_3709 lv4 = (class_3709)lv3.method_11614();

				for (class_2350 lv5 : class_2350.class_2353.field_11062) {
					if (lv4.method_19285(arg, lv3, arg.method_8321(lv2), new class_3965(new class_243(0.5, 0.5, 0.5), lv5, lv2, false), null, false)) {
						break;
					}
				}
			}
		}
	}
}
