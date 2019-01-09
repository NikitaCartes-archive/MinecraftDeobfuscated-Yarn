package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_2951 implements class_3037 {
	public final class_2680 field_13345;
	public final int field_13346;

	public class_2951(class_2680 arg, int i) {
		this.field_13345 = arg;
		this.field_13346 = i;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("state"),
					class_2680.method_16550(dynamicOps, this.field_13345).getValue(),
					dynamicOps.createString("start_radius"),
					dynamicOps.createInt(this.field_13346)
				)
			)
		);
	}

	public static <T> class_2951 method_12814(Dynamic<T> dynamic) {
		class_2680 lv = (class_2680)dynamic.get("state").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		int i = dynamic.get("start_radius").asInt(0);
		return new class_2951(lv, i);
	}
}
