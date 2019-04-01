package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.function.Predicate;

public class class_4096 extends class_4097<class_1309> {
	private final Predicate<class_4158> field_18330;
	private final class_4140<class_4208> field_18331;
	private final boolean field_18854;
	private long field_18332;

	public class_4096(class_4158 arg, class_4140<class_4208> arg2, boolean bl) {
		this.field_18330 = arg.method_19164();
		this.field_18331 = arg2;
		this.field_18854 = bl;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(this.field_18331, class_4141.field_18457));
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		return this.field_18854 && arg2.method_6109() ? false : arg.method_8510() - this.field_18332 >= 10L;
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		this.field_18332 = arg.method_8510();
		class_1314 lv = (class_1314)arg2;
		class_4153 lv2 = arg.method_19494();
		Predicate<class_2338> predicate = arg3 -> {
			class_2338.class_2339 lvx = new class_2338.class_2339(arg3);
			if (arg.method_8320(arg3.method_10074()).method_11588()) {
				lvx.method_10098(class_2350.field_11033);
			}

			while (arg.method_8320(lvx).method_11588()) {
				lvx.method_10098(class_2350.field_11033);
			}

			class_11 lv2x = lv.method_5942().method_6348(lvx.method_10062());
			return lv2x != null && lv2x.method_19315();
		};
		lv2.method_19131(this.field_18330, predicate, new class_2338(arg2), 48).ifPresent(arg3 -> {
			arg2.method_18868().method_18878(this.field_18331, class_4208.method_19443(arg.method_8597().method_12460(), arg3));
			class_4209.method_19778(arg, arg3);
		});
	}
}
