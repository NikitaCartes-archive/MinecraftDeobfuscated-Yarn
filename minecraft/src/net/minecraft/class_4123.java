package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class class_4123 extends class_4097<class_1309> {
	private long field_18848;

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		class_4208 lv = (class_4208)arg2.method_18868().method_19543(class_4140.field_18438).get();
		if (!Objects.equals(arg.method_8597().method_12460(), lv.method_19442())) {
			return false;
		} else {
			class_2680 lv2 = arg.method_8320(lv.method_19446());
			return lv.method_19446().method_19769(arg2.method_19538(), 2.0)
				&& lv2.method_11614().method_9525(class_3481.field_16443)
				&& !(Boolean)lv2.method_11654(class_2244.field_9968);
		}
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18438, class_4141.field_18456));
	}

	@Override
	protected boolean method_18927(class_3218 arg, class_1309 arg2, long l) {
		Optional<class_4208> optional = arg2.method_18868().method_19543(class_4140.field_18438);
		if (!optional.isPresent()) {
			return false;
		} else {
			class_2338 lv = ((class_4208)optional.get()).method_19446();
			return arg2.method_18868().method_18906(class_4168.field_18597)
				&& arg2.field_6010 > (double)lv.method_10264() + 0.4
				&& lv.method_19769(arg2.method_19538(), 1.14);
		}
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		if (l > this.field_18848) {
			arg2.method_18403(((class_4208)arg2.method_18868().method_19543(class_4140.field_18438).get()).method_19446());
		}
	}

	@Override
	protected boolean method_18915(long l) {
		return false;
	}

	@Override
	protected void method_18926(class_3218 arg, class_1309 arg2, long l) {
		if (arg2.method_6113()) {
			arg2.method_18400();
			this.field_18848 = l + 40L;
		}
	}
}
