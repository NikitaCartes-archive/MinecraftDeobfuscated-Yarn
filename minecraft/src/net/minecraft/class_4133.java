package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;

public class class_4133 extends class_4097<class_1646> {
	private int field_18404;
	private boolean field_18403;

	protected boolean method_19037(class_3218 arg, class_1646 arg2) {
		return this.method_19036(arg.method_8532() % 24000L, arg2.method_19186());
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(
			Pair.of(class_4140.field_18439, class_4141.field_18456),
			Pair.of(class_4140.field_18446, class_4141.field_18458),
			Pair.of(class_4140.field_18874, class_4141.field_18458)
		);
	}

	protected void method_19614(class_3218 arg, class_1646 arg2, long l) {
		this.field_18403 = false;
		this.field_18404 = 0;
		arg2.method_18868().method_18875(class_4140.field_18446);
	}

	protected void method_19039(class_3218 arg, class_1646 arg2, long l) {
		class_4095<class_1646> lv = arg2.method_18868();
		class_1646.class_4222 lv2 = (class_1646.class_4222)lv.method_19543(class_4140.field_18874).orElseGet(class_1646.class_4222::new);
		lv2.method_19626(l);
		lv.method_18878(class_4140.field_18874, lv2);
		if (!this.field_18403) {
			arg2.method_19182();
			this.field_18403 = true;
			arg2.method_19183();
			lv.method_19543(class_4140.field_18439).ifPresent(arg2x -> lv.method_18878(class_4140.field_18446, new class_4099(arg2x.method_19446())));
		}

		this.field_18404++;
	}

	protected boolean method_19040(class_3218 arg, class_1646 arg2, long l) {
		class_4208 lv = (class_4208)arg2.method_18868().method_19543(class_4140.field_18439).get();
		return this.field_18404 < 100
			&& Objects.equals(lv.method_19442(), arg.method_8597().method_12460())
			&& lv.method_19446().method_19769(arg2.method_19538(), 1.73);
	}

	private boolean method_19036(long l, long m) {
		return m == 0L || l < m || l > m + 3500L;
	}
}
