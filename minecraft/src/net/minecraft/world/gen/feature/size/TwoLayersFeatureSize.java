package net.minecraft.world.gen.feature.size;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;

public class TwoLayersFeatureSize extends FeatureSize {
	private final int field_24155;
	private final int field_24156;
	private final int field_24157;

	public TwoLayersFeatureSize(int i, int j, int k) {
		this(i, j, k, OptionalInt.empty());
	}

	public TwoLayersFeatureSize(int i, int j, int k, OptionalInt minClippedHeight) {
		super(FeatureSizeType.TWO_LAYERS_FEATURE_SIZE, minClippedHeight);
		this.field_24155 = i;
		this.field_24156 = j;
		this.field_24157 = k;
	}

	public <T> TwoLayersFeatureSize(Dynamic<T> dynamic) {
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
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("limit"), ops.createInt(this.field_24155))
			.put(ops.createString("lower_size"), ops.createInt(this.field_24156))
			.put(ops.createString("upper_size"), ops.createInt(this.field_24157));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
