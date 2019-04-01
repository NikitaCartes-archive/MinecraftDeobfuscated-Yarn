package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3823 extends class_3825 {
	private final class_2680 field_16878;
	private final float field_16877;

	public class_3823(class_2680 arg, float f) {
		this.field_16878 = arg;
		this.field_16877 = f;
	}

	public <T> class_3823(Dynamic<T> dynamic) {
		this(class_2680.method_11633(dynamic.get("blockstate").orElseEmptyMap()), dynamic.get("probability").asFloat(1.0F));
	}

	@Override
	public boolean method_16768(class_2680 arg, Random random) {
		return arg == this.field_16878 && random.nextFloat() < this.field_16877;
	}

	@Override
	protected class_3827 method_16766() {
		return class_3827.field_16984;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("blockstate"),
					class_2680.method_16550(dynamicOps, this.field_16878).getValue(),
					dynamicOps.createString("probability"),
					dynamicOps.createFloat(this.field_16877)
				)
			)
		);
	}
}
