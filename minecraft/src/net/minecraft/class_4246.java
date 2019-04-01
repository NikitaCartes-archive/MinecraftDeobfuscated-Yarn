package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;

public class class_4246 extends class_4097<class_1309> {
	private final float field_18993;
	private final int field_18994;
	private final int field_18995;
	private Optional<class_2338> field_18998 = Optional.empty();

	public class_4246(int i, float f, int j) {
		this.field_18994 = i;
		this.field_18993 = f;
		this.field_18995 = j;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(
			Pair.of(class_4140.field_18445, class_4141.field_18457),
			Pair.of(class_4140.field_18438, class_4141.field_18458),
			Pair.of(class_4140.field_19008, class_4141.field_18458)
		);
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		Optional<class_2338> optional = arg.method_19494()
			.method_19127(argx -> argx == class_4158.field_18517, argx -> true, new class_2338(arg2), this.field_18995 + 1, class_4153.class_4155.field_18489);
		if (optional.isPresent() && ((class_2338)optional.get()).method_19769(arg2.method_19538(), (double)this.field_18995)) {
			this.field_18998 = optional;
		} else {
			this.field_18998 = Optional.empty();
		}

		return true;
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		Optional<class_2338> optional = this.field_18998;
		if (!optional.isPresent()) {
			optional = arg.method_19494()
				.method_20005(
					argx -> argx == class_4158.field_18517, argx -> true, class_4153.class_4155.field_18489, new class_2338(arg2), this.field_18994, arg2.method_6051()
				);
			if (!optional.isPresent()) {
				Optional<class_4208> optional2 = lv.method_19543(class_4140.field_18438);
				if (optional2.isPresent()) {
					optional = Optional.of(((class_4208)optional2.get()).method_19446());
				}
			}
		}

		if (optional.isPresent()) {
			lv.method_18875(class_4140.field_18449);
			lv.method_18875(class_4140.field_18446);
			lv.method_18875(class_4140.field_18448);
			lv.method_18875(class_4140.field_18447);
			lv.method_18878(class_4140.field_19008, class_4208.method_19443(arg.method_8597().method_12460(), (class_2338)optional.get()));
			if (!((class_2338)optional.get()).method_19769(arg2.method_19538(), (double)this.field_18995)) {
				lv.method_18878(class_4140.field_18445, new class_4142((class_2338)optional.get(), this.field_18993, this.field_18995));
			}
		}
	}
}
