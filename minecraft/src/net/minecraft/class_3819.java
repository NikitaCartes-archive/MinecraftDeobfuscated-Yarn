package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3819 extends class_3825 {
	private final class_2248 field_16869;

	public class_3819(class_2248 arg) {
		this.field_16869 = arg;
	}

	public <T> class_3819(Dynamic<T> dynamic) {
		this(class_2378.field_11146.method_10223(new class_2960(dynamic.get("block").asString(""))));
	}

	@Override
	public boolean method_16768(class_2680 arg, Random random) {
		return arg.method_11614() == this.field_16869;
	}

	@Override
	protected class_3827 method_16766() {
		return class_3827.field_16981;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("block"), dynamicOps.createString(class_2378.field_11146.method_10221(this.field_16869).toString()))
			)
		);
	}
}
