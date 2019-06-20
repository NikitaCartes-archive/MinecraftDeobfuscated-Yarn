package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public class class_4248 extends class_4097<class_1309> {
	private final float field_18999;

	public class_4248(float f) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18457));
		this.field_18999 = f;
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		Optional<class_243> optional = Optional.ofNullable(this.method_19987(arg, arg2));
		if (optional.isPresent()) {
			arg2.method_18868().method_18879(class_4140.field_18445, optional.map(argx -> new class_4142(argx, this.field_18999, 0)));
		}
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		return !arg.method_8311(new class_2338(arg2.field_5987, arg2.method_5829().field_1322, arg2.field_6035));
	}

	@Nullable
	private class_243 method_19987(class_3218 arg, class_1309 arg2) {
		Random random = arg2.method_6051();
		class_2338 lv = new class_2338(arg2.field_5987, arg2.method_5829().field_1322, arg2.field_6035);

		for (int i = 0; i < 10; i++) {
			class_2338 lv2 = lv.method_10069(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (method_20497(arg, arg2)) {
				return new class_243((double)lv2.method_10263(), (double)lv2.method_10264(), (double)lv2.method_10260());
			}
		}

		return null;
	}

	public static boolean method_20497(class_3218 arg, class_1309 arg2) {
		return arg.method_8311(new class_2338(arg2))
			&& (double)arg.method_8598(class_2902.class_2903.field_13197, new class_2338(arg2)).method_10264() <= arg2.field_6010;
	}
}
