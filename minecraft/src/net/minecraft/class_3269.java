package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3269 implements class_2998 {
	protected final class_2893.class_2894 field_14198;
	protected final float field_14197;

	public class_3269(class_2893.class_2894 arg, float f) {
		this.field_14198 = arg;
		this.field_14197 = f;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("step"),
					dynamicOps.createString(this.field_14198.toString()),
					dynamicOps.createString("probability"),
					dynamicOps.createFloat(this.field_14197)
				)
			)
		);
	}

	public static class_3269 method_14419(Dynamic<?> dynamic) {
		class_2893.class_2894 lv = class_2893.class_2894.valueOf(dynamic.get("step").asString(""));
		float f = dynamic.get("probability").asFloat(0.0F);
		return new class_3269(lv, f);
	}
}
