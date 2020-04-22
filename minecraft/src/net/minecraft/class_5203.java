package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;

public class class_5203 extends class_5201 {
	private final int field_24150;
	private final int field_24151;
	private final int field_24152;
	private final int field_24153;
	private final int field_24154;

	public class_5203(int i, int j, int k, int l, int m, OptionalInt optionalInt) {
		super(class_5202.field_24148, optionalInt);
		this.field_24150 = i;
		this.field_24151 = j;
		this.field_24152 = k;
		this.field_24153 = l;
		this.field_24154 = m;
	}

	public <T> class_5203(Dynamic<T> dynamic) {
		this(
			dynamic.get("limit").asInt(1),
			dynamic.get("upper_limit").asInt(1),
			dynamic.get("lower_size").asInt(0),
			dynamic.get("middle_size").asInt(1),
			dynamic.get("upper_size").asInt(1),
			(OptionalInt)dynamic.get("min_clipped_height").asNumber().map(number -> OptionalInt.of(number.intValue())).orElse(OptionalInt.empty())
		);
	}

	@Override
	public int method_27378(int i, int j) {
		if (j < this.field_24150) {
			return this.field_24152;
		} else {
			return j >= i - this.field_24151 ? this.field_24154 : this.field_24153;
		}
	}

	@Override
	public <T> T method_27380(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("limit"), dynamicOps.createInt(this.field_24150))
			.put(dynamicOps.createString("upper_limit"), dynamicOps.createInt(this.field_24151))
			.put(dynamicOps.createString("lower_size"), dynamicOps.createInt(this.field_24152))
			.put(dynamicOps.createString("middle_size"), dynamicOps.createInt(this.field_24153))
			.put(dynamicOps.createString("upper_size"), dynamicOps.createInt(this.field_24154));
		return dynamicOps.merge(super.method_27380(dynamicOps), dynamicOps.createMap(builder.build()));
	}
}
