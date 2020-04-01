package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class EmeraldOreFeatureConfig implements FeatureConfig {
	public final BlockState target;
	public final BlockState state;

	public EmeraldOreFeatureConfig(BlockState target, BlockState state) {
		this.target = target;
		this.state = state;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("target"), BlockState.serialize(ops, this.target).getValue(), ops.createString("state"), BlockState.serialize(ops, this.state).getValue()
				)
			)
		);
	}

	public static <T> EmeraldOreFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		return new EmeraldOreFeatureConfig(blockState, blockState2);
	}

	public static EmeraldOreFeatureConfig method_26630(Random random) {
		return new EmeraldOreFeatureConfig(Registry.BLOCK.getRandom(random).getDefaultState(), Registry.BLOCK.getRandom(random).getDefaultState());
	}
}
