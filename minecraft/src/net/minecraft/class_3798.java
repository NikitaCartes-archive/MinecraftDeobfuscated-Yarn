package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3798 extends class_3825 {
	private final class_3494<class_2248> field_16747;

	public class_3798(class_3494<class_2248> arg) {
		this.field_16747 = arg;
	}

	public <T> class_3798(Dynamic<T> dynamic) {
		this(class_3481.method_15073().method_15193(new class_2960(dynamic.get("tag").asString(""))));
	}

	@Override
	public boolean method_16768(class_2680 arg, Random random) {
		return arg.method_11602(this.field_16747);
	}

	@Override
	protected class_3827 method_16766() {
		return class_3827.field_16983;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("tag"), dynamicOps.createString(this.field_16747.method_15143().toString())))
		);
	}
}
