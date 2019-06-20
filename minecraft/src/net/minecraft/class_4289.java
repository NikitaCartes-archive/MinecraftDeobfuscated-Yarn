package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class class_4289 extends class_4097<class_1314> {
	private final float field_19258;

	public class_4289(float f) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18457));
		this.field_19258 = f;
	}

	protected boolean method_20421(class_3218 arg, class_1314 arg2) {
		return !arg.method_8311(new class_2338(arg2));
	}

	protected void method_20422(class_3218 arg, class_1314 arg2, long l) {
		class_2338 lv = new class_2338(arg2);
		List<class_2338> list = (List<class_2338>)class_2338.method_20437(lv.method_10069(-1, -1, -1), lv.method_10069(1, 1, 1))
			.map(class_2338::method_10062)
			.collect(Collectors.toList());
		Collections.shuffle(list);
		Optional<class_2338> optional = list.stream()
			.filter(arg2x -> !arg.method_8311(arg2x))
			.filter(arg3 -> arg.method_8515(arg3, arg2))
			.filter(arg3 -> arg.method_17892(arg2))
			.findFirst();
		optional.ifPresent(arg2x -> arg2.method_18868().method_18878(class_4140.field_18445, new class_4142(arg2x, this.field_19258, 0)));
	}
}
