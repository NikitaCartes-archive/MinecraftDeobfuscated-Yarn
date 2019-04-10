package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class FillLayerFeatureConfig implements FeatureConfig {
	public final int height;
	public final BlockState state;

	public FillLayerFeatureConfig(int i, BlockState blockState) {
		this.height = i;
		this.state = blockState;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("height"),
					dynamicOps.createInt(this.height),
					dynamicOps.createString("state"),
					BlockState.serialize(dynamicOps, this.state).getValue()
				)
			)
		);
	}

	public static <T> FillLayerFeatureConfig deserialize(Dynamic<T> dynamic) {
		int i = dynamic.get("height").asInt(0);
		BlockState blockState = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		return new FillLayerFeatureConfig(i, blockState);
	}
}
