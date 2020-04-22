package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;

public class class_5204 extends class_5201 {
	private final int field_24155;
	private final int field_24156;
	private final int field_24157;

	public class_5204(int i, int j, int k) {
		this(i, j, k, OptionalInt.empty());
	}

	public class_5204(int i, int j, int k, OptionalInt optionalInt) {
		super(class_5202.field_24147, optionalInt);
		this.field_24155 = i;
		this.field_24156 = j;
		this.field_24157 = k;
	}

	public <T> class_5204(Dynamic<T> dynamic) {
		this(
			dynamic.get("limit").asInt(1),
			dynamic.get("lower_size").asInt(0),
			dynamic.get("upper_size").asInt(1),
			(OptionalInt)dynamic.get("min_clipped_height").asNumber().map(number -> OptionalInt.of(number.intValue())).orElse(OptionalInt.empty())
		);
	}

	@Override
	public int method_27378(int i, int j) {
		return j < this.field_24155 ? this.field_24156 : this.field_24157;
	}

	@Override
	public <T> T method_27380(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("limit"), dynamicOps.createInt(this.field_24155))
			.put(dynamicOps.createString("lower_size"), dynamicOps.createInt(this.field_24156))
			.put(dynamicOps.createString("upper_size"), dynamicOps.createInt(this.field_24157));
		return dynamicOps.merge(super.method_27380(dynamicOps), dynamicOps.createMap(builder.build()));
	}
}
