package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class SingleStateFeatureConfig implements FeatureConfig {
	public final BlockState state;

	public SingleStateFeatureConfig(BlockState state) {
		this.state = state;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
	}

	public static <T> SingleStateFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		return new SingleStateFeatureConfig(blockState);
	}

	public static SingleStateFeatureConfig method_26597(Random random) {
		return new SingleStateFeatureConfig(Util.method_26719(random, OverworldChunkGeneratorConfig.field_23567));
	}
}
