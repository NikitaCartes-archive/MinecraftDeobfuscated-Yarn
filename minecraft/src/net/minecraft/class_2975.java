package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_2975<FC extends class_3037> {
	public final class_3031<FC> field_13376;
	public final FC field_13375;

	public class_2975(class_3031<FC> arg, FC arg2) {
		this.field_13376 = arg;
		this.field_13375 = arg2;
	}

	public class_2975(class_3031<FC> arg, Dynamic<?> dynamic) {
		this(arg, arg.method_13148(dynamic));
	}

	public <T> Dynamic<T> method_16584(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(class_2378.field_11138.method_10221(this.field_13376).toString()),
					dynamicOps.createString("config"),
					this.field_13375.method_16587(dynamicOps).getValue()
				)
			)
		);
	}

	public boolean method_12862(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3) {
		return this.field_13376.method_13151(arg, arg2, random, arg3, this.field_13375);
	}

	public static <T> class_2975<?> method_12861(Dynamic<T> dynamic) {
		class_3031<? extends class_3037> lv = (class_3031<? extends class_3037>)class_2378.field_11138.method_10223(new class_2960(dynamic.get("name").asString("")));
		return new class_2975<>(lv, dynamic.get("config").orElseEmptyMap());
	}
}
