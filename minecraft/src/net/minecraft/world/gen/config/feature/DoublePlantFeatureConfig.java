package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class DoublePlantFeatureConfig implements FeatureConfig {
	public final BlockState state;

	public DoublePlantFeatureConfig(BlockState blockState) {
		this.state = blockState;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue()))
		);
	}

	public static <T> DoublePlantFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		return new DoublePlantFeatureConfig(blockState);
	}
}
