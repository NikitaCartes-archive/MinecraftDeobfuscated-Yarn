package net.minecraft.world.gen.feature.size;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;

public class ThreeLayersFeatureSize extends FeatureSize {
	private final int limit;
	private final int upperLimit;
	private final int lowerSize;
	private final int middleSize;
	private final int upperSize;

	public ThreeLayersFeatureSize(int limit, int upperLimit, int lowerSize, int middleSize, int upperSize, OptionalInt minClippedHeight) {
		super(FeatureSizeType.THREE_LAYERS_FEATURE_SIZE, minClippedHeight);
		this.limit = limit;
		this.upperLimit = upperLimit;
		this.lowerSize = lowerSize;
		this.middleSize = middleSize;
		this.upperSize = upperSize;
	}

	public <T> ThreeLayersFeatureSize(Dynamic<T> dynamic) {
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
		if (j < this.limit) {
			return this.lowerSize;
		} else {
			return j >= i - this.upperLimit ? this.upperSize : this.middleSize;
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("limit"), ops.createInt(this.limit))
			.put(ops.createString("upper_limit"), ops.createInt(this.upperLimit))
			.put(ops.createString("lower_size"), ops.createInt(this.lowerSize))
			.put(ops.createString("middle_size"), ops.createInt(this.middleSize))
			.put(ops.createString("upper_size"), ops.createInt(this.upperSize));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
