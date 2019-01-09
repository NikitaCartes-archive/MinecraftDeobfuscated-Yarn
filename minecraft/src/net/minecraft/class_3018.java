package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3018 implements class_3037 {
	private final boolean field_13475;

	public class_3018(boolean bl) {
		this.field_13475 = bl;
	}

	public boolean method_13026() {
		return this.field_13475;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("exits_at_spawn"), dynamicOps.createBoolean(this.field_13475))));
	}

	public static <T> class_3018 method_13027(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("exits_at_spawn").asBoolean(false);
		return new class_3018(bl);
	}
}
