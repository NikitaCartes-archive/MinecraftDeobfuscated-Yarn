package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class class_4122 extends class_4097<class_1646> {
	private final class_4140<class_4208> field_18382;
	private final float field_18383;
	private final int field_18384;
	private final int field_18385;

	public class_4122(class_4140<class_4208> arg, float f, int i, int j) {
		this.field_18382 = arg;
		this.field_18383 = f;
		this.field_18384 = i;
		this.field_18385 = j;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457), Pair.of(this.field_18382, class_4141.field_18456));
	}

	protected void method_19509(class_3218 arg, class_1646 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		lv.method_19543(this.field_18382).ifPresent(arg4 -> {
			if (this.method_19597(arg, arg2, arg4)) {
				arg2.method_19176(this.field_18382);
				lv.method_18875(this.field_18382);
			} else if (!this.method_19988(arg, arg2, arg4)) {
				lv.method_18878(class_4140.field_18445, new class_4142(arg4.method_19446(), this.field_18383, this.field_18384));
			}
		});
	}

	private boolean method_19597(class_3218 arg, class_1646 arg2, class_4208 arg3) {
		return arg3.method_19442() != arg.method_8597().method_12460() || arg3.method_19446().method_19455(new class_2338(arg2)) > this.field_18385;
	}

	private boolean method_19988(class_3218 arg, class_1646 arg2, class_4208 arg3) {
		return arg3.method_19442() == arg.method_8597().method_12460() && arg3.method_19446().method_19455(new class_2338(arg2)) <= this.field_18384;
	}
}
