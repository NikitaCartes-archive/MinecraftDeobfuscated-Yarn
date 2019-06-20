package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3061 implements class_3037 {
	public final boolean field_13661;

	public class_3061(boolean bl) {
		this.field_13661 = bl;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("inside_rock"), dynamicOps.createBoolean(this.field_13661))));
	}

	public static <T> class_3061 method_13379(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("inside_rock").asBoolean(false);
		return new class_3061(bl);
	}
}
