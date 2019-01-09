package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3101 implements class_3037 {
	public final double field_13693;
	public final class_3098.class_3100 field_13694;

	public class_3101(double d, class_3098.class_3100 arg) {
		this.field_13693 = d;
		this.field_13694 = arg;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("probability"),
					dynamicOps.createDouble(this.field_13693),
					dynamicOps.createString("type"),
					dynamicOps.createString(this.field_13694.method_13534())
				)
			)
		);
	}

	public static <T> class_3101 method_13536(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		class_3098.class_3100 lv = class_3098.class_3100.method_13532(dynamic.get("type").asString(""));
		return new class_3101((double)f, lv);
	}
}
