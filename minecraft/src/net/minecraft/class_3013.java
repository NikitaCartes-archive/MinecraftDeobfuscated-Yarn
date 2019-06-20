package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class class_3013 implements class_3037 {
	public final class_2680 field_13470;
	public final int field_13472;
	public final int field_13471;
	public final List<class_2680> field_13469;

	public class_3013(class_2680 arg, int i, int j, List<class_2680> list) {
		this.field_13470 = arg;
		this.field_13472 = i;
		this.field_13471 = j;
		this.field_13469 = list;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("state"),
					class_2680.method_16550(dynamicOps, this.field_13470).getValue(),
					dynamicOps.createString("radius"),
					dynamicOps.createInt(this.field_13472),
					dynamicOps.createString("y_size"),
					dynamicOps.createInt(this.field_13471),
					dynamicOps.createString("targets"),
					dynamicOps.createList(this.field_13469.stream().map(arg -> class_2680.method_16550(dynamicOps, arg).getValue()))
				)
			)
		);
	}

	public static <T> class_3013 method_13012(Dynamic<T> dynamic) {
		class_2680 lv = (class_2680)dynamic.get("state").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		int i = dynamic.get("radius").asInt(0);
		int j = dynamic.get("y_size").asInt(0);
		List<class_2680> list = dynamic.get("targets").asList(class_2680::method_11633);
		return new class_3013(lv, i, j, list);
	}
}
