package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3243<DC extends class_2998> {
	public final class_3284<DC> field_14115;
	public final DC field_14114;

	public class_3243(class_3284<DC> arg, Dynamic<?> dynamic) {
		this(arg, arg.method_14451(dynamic));
	}

	public class_3243(class_3284<DC> arg, DC arg2) {
		this.field_14115 = arg;
		this.field_14114 = arg2;
	}

	public <FC extends class_3037> boolean method_14358(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_2975<FC> arg4) {
		return this.field_14115.method_15927(arg, arg2, random, arg3, this.field_14114, arg4);
	}

	public <T> Dynamic<T> method_16641(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(class_2378.field_11148.method_10221(this.field_14115).toString()),
					dynamicOps.createString("config"),
					this.field_14114.method_16585(dynamicOps).getValue()
				)
			)
		);
	}

	public static <T> class_3243<?> method_14359(Dynamic<T> dynamic) {
		class_3284<? extends class_2998> lv = (class_3284<? extends class_2998>)class_2378.field_11148.method_10223(new class_2960(dynamic.get("name").asString("")));
		return new class_3243<>(lv, dynamic.get("config").orElseEmptyMap());
	}
}
