package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;

public class class_4290 extends class_4097<class_1309> {
	private final float field_19259;
	private long field_19260;

	public class_4290(float f) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18457, class_4140.field_18438, class_4141.field_18457));
		this.field_19259 = f;
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		if (arg.method_8510() - this.field_19260 < 40L) {
			return false;
		} else {
			class_1314 lv = (class_1314)arg2;
			class_4153 lv2 = arg.method_19494();
			Optional<class_2338> optional = lv2.method_20006(
				class_4158.field_18517.method_19164(), argx -> true, new class_2338(arg2), 48, class_4153.class_4155.field_18489
			);
			return optional.isPresent() && !(((class_2338)optional.get()).method_10262(new class_2382(lv.field_5987, lv.field_6010, lv.field_6035)) <= 4.0);
		}
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		this.field_19260 = arg.method_8510();
		class_1314 lv = (class_1314)arg2;
		class_4153 lv2 = arg.method_19494();
		Predicate<class_2338> predicate = arg3 -> {
			class_2338.class_2339 lvx = new class_2338.class_2339(arg3);
			if (arg.method_8320(arg3.method_10074()).method_11588()) {
				lvx.method_10098(class_2350.field_11033);
			}

			while (arg.method_8320(lvx).method_11588() && lvx.method_10264() >= 0) {
				lvx.method_10098(class_2350.field_11033);
			}

			class_11 lv2x = lv.method_5942().method_6348(lvx);
			return lv2x != null && lv2x.method_19313(lvx);
		};
		lv2.method_20006(class_4158.field_18517.method_19164(), predicate, new class_2338(arg2), 48, class_4153.class_4155.field_18489).ifPresent(arg3 -> {
			arg2.method_18868().method_18878(class_4140.field_18445, new class_4142(arg3, this.field_19259, 1));
			class_4209.method_19778(arg, arg3);
		});
	}
}
