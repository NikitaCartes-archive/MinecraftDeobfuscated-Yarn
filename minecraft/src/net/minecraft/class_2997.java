package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_2997 implements class_2998 {
	public final int field_13435;
	public final int field_13434;
	public final int field_13433;
	public final int field_13432;

	public class_2997(int i, int j, int k, int l) {
		this.field_13435 = i;
		this.field_13434 = j;
		this.field_13433 = k;
		this.field_13432 = l;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.field_13435),
					dynamicOps.createString("bottom_offset"),
					dynamicOps.createInt(this.field_13434),
					dynamicOps.createString("top_offset"),
					dynamicOps.createInt(this.field_13433),
					dynamicOps.createString("maximum"),
					dynamicOps.createInt(this.field_13432)
				)
			)
		);
	}

	public static class_2997 method_12942(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		int j = dynamic.get("bottom_offset").asInt(0);
		int k = dynamic.get("top_offset").asInt(0);
		int l = dynamic.get("maximum").asInt(0);
		return new class_2997(i, j, k, l);
	}
}
