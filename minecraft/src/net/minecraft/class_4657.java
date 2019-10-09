package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class class_4657 extends class_4651 {
	private final WeightedList<BlockState> field_21315;

	private class_4657(WeightedList<BlockState> weightedList) {
		super(class_4652.WEIGHTED_STATE_PROVIDER);
		this.field_21315 = weightedList;
	}

	public class_4657() {
		this(new WeightedList<>());
	}

	public <T> class_4657(Dynamic<T> dynamic) {
		this(new WeightedList<>(dynamic.get("entries").orElseEmptyList(), BlockState::deserialize));
	}

	public class_4657 method_23458(BlockState blockState, int i) {
		this.field_21315.add(blockState, i);
		return this;
	}

	@Override
	public BlockState method_23455(Random random, BlockPos blockPos) {
		return this.field_21315.method_23337(random);
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.field_21304).toString()))
			.put(dynamicOps.createString("entries"), this.field_21315.method_23330(dynamicOps, blockState -> BlockState.serialize(dynamicOps, blockState)));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
