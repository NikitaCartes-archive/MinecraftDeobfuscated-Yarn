package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.stream.Collectors;

public class class_4126 extends class_4097<class_1646> {
	private Set<class_1792> field_18389 = ImmutableSet.of();

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18447, class_4141.field_18456), Pair.of(class_4140.field_18442, class_4141.field_18456));
	}

	protected boolean method_19015(class_3218 arg, class_1646 arg2) {
		return class_4215.method_19551(arg2.method_18868(), class_4140.field_18447, class_1299.field_6077);
	}

	protected boolean method_19016(class_3218 arg, class_1646 arg2, long l) {
		return this.method_19015(arg, arg2);
	}

	protected void method_19017(class_3218 arg, class_1646 arg2, long l) {
		class_1646 lv = (class_1646)arg2.method_18868().method_19543(class_4140.field_18447).get();
		class_4215.method_19548(arg2, lv);
		this.field_18389 = method_19611(arg2, lv);
	}

	protected void method_19018(class_3218 arg, class_1646 arg2, long l) {
		class_1646 lv = (class_1646)arg2.method_18868().method_19543(class_4140.field_18447).get();
		if (!(arg2.method_5858(lv) > 5.0)) {
			class_4215.method_19548(arg2, lv);
			arg2.method_19177(lv, l);
			if (arg2.method_7234() && lv.method_7239()) {
				method_19013(arg2, class_1646.field_18526.keySet(), lv);
			}

			if (!this.field_18389.isEmpty() && arg2.method_18011().method_18862(this.field_18389)) {
				method_19013(arg2, this.field_18389, lv);
			}
		}
	}

	protected void method_19019(class_3218 arg, class_1646 arg2, long l) {
		arg2.method_18868().method_18875(class_4140.field_18447);
	}

	private static Set<class_1792> method_19611(class_1646 arg, class_1646 arg2) {
		ImmutableSet<class_1792> immutableSet = arg2.method_7231().method_16924().method_19199();
		ImmutableSet<class_1792> immutableSet2 = arg.method_7231().method_16924().method_19199();
		return (Set<class_1792>)immutableSet.stream().filter(argx -> !immutableSet2.contains(argx)).collect(Collectors.toSet());
	}

	private static void method_19013(class_1646 arg, Set<class_1792> set, class_1309 arg2) {
		class_1277 lv = arg.method_18011();
		class_1799 lv2 = class_1799.field_8037;

		for (int i = 0; i < lv.method_5439(); i++) {
			class_1799 lv3 = lv.method_5438(i);
			if (!lv3.method_7960()) {
				class_1792 lv4 = lv3.method_7909();
				if (set.contains(lv4)) {
					int j = lv3.method_7947() / 2;
					lv3.method_7934(j);
					lv2 = new class_1799(lv4, j);
					break;
				}
			}
		}

		if (!lv2.method_7960()) {
			class_4215.method_19949(arg, lv2, arg2);
		}
	}
}
