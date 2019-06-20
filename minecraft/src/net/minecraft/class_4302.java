package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_4302 implements class_3037 {
	public final boolean field_19317;

	public class_4302(boolean bl) {
		this.field_19317 = bl;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("planted"), dynamicOps.createBoolean(this.field_19317))));
	}

	public static <T> class_4302 method_20531(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("planted").asBoolean(false);
		return new class_4302(bl);
	}
}
