package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class FillLayerFeatureConfig implements FeatureConfig {
	public final int height;
	public final BlockState state;

	public FillLayerFeatureConfig(int height, BlockState state) {
		this.height = height;
		this.state = state;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(ops.createString("height"), ops.createInt(this.height), ops.createString("state"), BlockState.serialize(ops, this.state).getValue())
			)
		);
	}

	public static <T> FillLayerFeatureConfig deserialize(Dynamic<T> dynamic) {
		int i = dynamic.get("height").asInt(0);
		BlockState blockState = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		return new FillLayerFeatureConfig(i, blockState);
	}

	public static FillLayerFeatureConfig method_26614(Random random) {
		return new FillLayerFeatureConfig(random.nextInt(5), Registry.BLOCK.getRandom(random).getDefaultState());
	}
}
