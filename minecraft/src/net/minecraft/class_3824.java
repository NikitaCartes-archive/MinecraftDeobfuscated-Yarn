package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3824 extends class_3825 {
	private final class_2248 field_16880;
	private final float field_16879;

	public class_3824(class_2248 arg, float f) {
		this.field_16880 = arg;
		this.field_16879 = f;
	}

	public <T> class_3824(Dynamic<T> dynamic) {
		this(class_2378.field_11146.method_10223(new class_2960(dynamic.get("block").asString(""))), dynamic.get("probability").asFloat(1.0F));
	}

	@Override
	public boolean method_16768(class_2680 arg, Random random) {
		return arg.method_11614() == this.field_16880 && random.nextFloat() < this.field_16879;
	}

	@Override
	protected class_3827 method_16766() {
		return class_3827.field_16980;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("block"),
					dynamicOps.createString(class_2378.field_11146.method_10221(this.field_16880).toString()),
					dynamicOps.createString("probability"),
					dynamicOps.createFloat(this.field_16879)
				)
			)
		);
	}
}
