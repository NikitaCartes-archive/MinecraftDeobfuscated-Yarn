package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class EmeraldOreFeatureConfig implements FeatureConfig {
	public final BlockState target;
	public final BlockState state;

	public EmeraldOreFeatureConfig(BlockState blockState, BlockState blockState2) {
		this.target = blockState;
		this.state = blockState2;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("target"),
					BlockState.serialize(dynamicOps, this.target).getValue(),
					dynamicOps.createString("state"),
					BlockState.serialize(dynamicOps, this.state).getValue()
				)
			)
		);
	}

	public static <T> EmeraldOreFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		return new EmeraldOreFeatureConfig(blockState, blockState2);
	}
}
