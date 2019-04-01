package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3820 extends class_3825 {
	private final class_2680 field_16870;

	public class_3820(class_2680 arg) {
		this.field_16870 = arg;
	}

	public <T> class_3820(Dynamic<T> dynamic) {
		this(class_2680.method_11633(dynamic.get("blockstate").orElseEmptyMap()));
	}

	@Override
	public boolean method_16768(class_2680 arg, Random random) {
		return arg == this.field_16870;
	}

	@Override
	protected class_3827 method_16766() {
		return class_3827.field_16985;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("blockstate"), class_2680.method_16550(dynamicOps, this.field_16870).getValue()))
		);
	}
}
