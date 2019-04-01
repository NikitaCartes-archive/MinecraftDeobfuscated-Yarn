package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;

public class class_4252 extends class_4097<class_1309> {
	private final int field_19154;
	private final int field_19000;
	private int field_19001;

	public class_4252(int i, int j) {
		this.field_19000 = i * 20;
		this.field_19001 = 0;
		this.field_19154 = j;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_19008, class_4141.field_18456), Pair.of(class_4140.field_19009, class_4141.field_18456));
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		Optional<Long> optional = lv.method_19543(class_4140.field_19009);
		boolean bl = (Long)optional.get() + 300L <= l;
		if (this.field_19001 <= this.field_19000 && !bl) {
			class_2338 lv2 = ((class_4208)lv.method_19543(class_4140.field_19008).get()).method_19446();
			if (lv2.method_19771(new class_2338(arg2), (double)(this.field_19154 + 1))) {
				this.field_19001++;
			}
		} else {
			lv.method_18875(class_4140.field_19009);
			lv.method_18875(class_4140.field_19008);
			lv.method_18871(arg.method_8532(), arg.method_8510());
			this.field_19001 = 0;
		}
	}
}
