package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_4279 implements class_3037 {
	public final int field_19202;
	public final class_2680 field_19203;

	public class_4279(int i, class_2680 arg) {
		this.field_19202 = i;
		this.field_19203 = arg;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("height"),
					dynamicOps.createInt(this.field_19202),
					dynamicOps.createString("state"),
					class_2680.method_16550(dynamicOps, this.field_19203).getValue()
				)
			)
		);
	}

	public static <T> class_4279 method_20313(Dynamic<T> dynamic) {
		int i = dynamic.get("height").asInt(0);
		class_2680 lv = (class_2680)dynamic.get("state").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		return new class_4279(i, lv);
	}
}
