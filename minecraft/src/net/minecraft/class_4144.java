package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class class_4144 extends class_4148<class_1309> {
	@Override
	public void method_19101(class_3218 arg, class_1309 arg2) {
		class_4095<?> lv = arg2.method_18868();
		if (arg2.method_6081() != null) {
			lv.method_18878(class_4140.field_18451, arg2.method_6081());
			class_1297 lv2 = ((class_1282)lv.method_19543(class_4140.field_18451).get()).method_5529();
			if (lv2 instanceof class_1309) {
				lv.method_18878(class_4140.field_18452, (class_1309)lv2);
			}
		} else {
			lv.method_18875(class_4140.field_18451);
		}
	}

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18451, class_4140.field_18452);
	}
}
