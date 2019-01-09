package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3812 implements class_3037 {
	public final class_2960 field_16861;
	public final int field_16860;

	public class_3812(String string, int i) {
		this.field_16861 = new class_2960(string);
		this.field_16860 = i;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("start_pool"),
					dynamicOps.createString(this.field_16861.toString()),
					dynamicOps.createString("size"),
					dynamicOps.createInt(this.field_16860)
				)
			)
		);
	}

	public static <T> class_3812 method_16752(Dynamic<T> dynamic) {
		String string = dynamic.get("start_pool").asString("");
		int i = dynamic.get("size").asInt(6);
		return new class_3812(string, i);
	}
}
