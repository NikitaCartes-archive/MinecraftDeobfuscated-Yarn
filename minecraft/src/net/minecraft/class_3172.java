package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3172 implements class_3037 {
	public final boolean field_13803;

	public class_3172(boolean bl) {
		this.field_13803 = bl;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("is_beached"), dynamicOps.createBoolean(this.field_13803))));
	}

	public static <T> class_3172 method_13928(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("is_beached").asBoolean(false);
		return new class_3172(bl);
	}
}
