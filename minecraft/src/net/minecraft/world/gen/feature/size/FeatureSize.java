package net.minecraft.world.gen.feature.size;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;

public abstract class FeatureSize {
	protected final FeatureSizeType<?> type;
	private final OptionalInt minClippedHeight;

	public FeatureSize(FeatureSizeType<?> type, OptionalInt minClippedHeight) {
		this.type = type;
		this.minClippedHeight = minClippedHeight;
	}

	public abstract int method_27378(int i, int j);

	public OptionalInt getMinClippedHeight() {
		return this.minClippedHeight;
	}

	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("type"), ops.createString(Registry.FEATURE_SIZE_TYPE.getId(this.type).toString()));
		this.minClippedHeight.ifPresent(i -> builder.put(ops.createString("min_clipped_height"), ops.createInt(i)));
		return new Dynamic<>(ops, ops.createMap(builder.build())).getValue();
	}
}
