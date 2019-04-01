package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class class_4121 extends class_4097<class_1314> {
	private final class_4140<? extends class_1297> field_18380;
	private final float field_18381;

	public class_4121(class_4140<? extends class_1297> arg, float f) {
		this.field_18380 = arg;
		this.field_18381 = f;
	}

	protected boolean method_19002(class_3218 arg, class_1314 arg2) {
		class_1297 lv = (class_1297)arg2.method_18868().method_19543(this.field_18380).get();
		return arg2.method_5858(lv) < 16.0;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457), Pair.of(this.field_18380, class_4141.field_18456));
	}

	protected void method_19003(class_3218 arg, class_1314 arg2, long l) {
		class_1297 lv = (class_1297)arg2.method_18868().method_19543(this.field_18380).get();
		method_19596(arg2, lv, this.field_18381);
	}

	public static void method_19596(class_1314 arg, class_1297 arg2, float f) {
		for (int i = 0; i < 10; i++) {
			class_243 lv = new class_243(arg.field_5987, arg.field_6010, arg.field_6035);
			class_243 lv2 = new class_243(arg2.field_5987, arg2.field_6010, arg2.field_6035);
			class_243 lv3 = lv.method_1020(lv2).method_1029();
			class_243 lv4 = class_1414.method_6377(arg, 16, 7, lv3, (float) (Math.PI / 10));
			if (lv4 != null) {
				arg.method_18868().method_18878(class_4140.field_18445, new class_4142(lv4, f, 0));
				return;
			}
		}
	}
}
