package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;

public class class_3795 extends class_3491 {
	private final class_2902.class_2903 field_16723;
	private final int field_16725;

	public class_3795(class_2902.class_2903 arg, int i) {
		this.field_16723 = arg;
		this.field_16725 = i;
	}

	public class_3795(Dynamic<?> dynamic) {
		this(class_2902.class_2903.method_12609(dynamic.get("heightmap").asString(class_2902.class_2903.field_13194.method_12605())), dynamic.get("offset").asInt(0));
	}

	@Nullable
	@Override
	public class_3499.class_3501 method_15110(class_1941 arg, class_2338 arg2, class_3499.class_3501 arg3, class_3499.class_3501 arg4, class_3492 arg5) {
		int i = arg.method_8589(this.field_16723, arg4.field_15597.method_10263(), arg4.field_15597.method_10260()) + this.field_16725;
		int j = arg3.field_15597.method_10264();
		return new class_3499.class_3501(new class_2338(arg4.field_15597.method_10263(), i + j, arg4.field_15597.method_10260()), arg4.field_15596, arg4.field_15595);
	}

	@Override
	protected class_3828 method_16772() {
		return class_3828.field_16989;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("heightmap"),
					dynamicOps.createString(this.field_16723.method_12605()),
					dynamicOps.createString("offset"),
					dynamicOps.createInt(this.field_16725)
				)
			)
		);
	}
}
