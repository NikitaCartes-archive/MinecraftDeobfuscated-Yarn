package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3226<FC extends class_3037> {
	public final class_3031<FC> field_14013;
	public final FC field_14012;
	public final Float field_14011;

	public class_3226(class_3031<FC> arg, FC arg2, Float float_) {
		this.field_14013 = arg;
		this.field_14012 = arg2;
		this.field_14011 = float_;
	}

	public class_3226(class_3031<FC> arg, Dynamic<?> dynamic, float f) {
		this(arg, arg.method_13148(dynamic), Float.valueOf(f));
	}

	public <T> Dynamic<T> method_16599(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(class_2378.field_11138.method_10221(this.field_14013).toString()),
					dynamicOps.createString("config"),
					this.field_14012.method_16587(dynamicOps).getValue(),
					dynamicOps.createString("chance"),
					dynamicOps.createFloat(this.field_14011)
				)
			)
		);
	}

	public boolean method_14271(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3) {
		return this.field_14013.method_13151(arg, arg2, random, arg3, this.field_14012);
	}

	public static <T> class_3226<?> method_14270(Dynamic<T> dynamic) {
		class_3031<? extends class_3037> lv = (class_3031<? extends class_3037>)class_2378.field_11138.method_10223(new class_2960(dynamic.get("name").asString("")));
		return new class_3226<>(lv, dynamic.get("config").orElseEmptyMap(), dynamic.get("chance").asFloat(0.0F));
	}
}
