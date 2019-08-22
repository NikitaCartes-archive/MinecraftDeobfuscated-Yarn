package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class DiskFeatureConfig implements FeatureConfig {
	public final BlockState state;
	public final int radius;
	public final int ySize;
	public final List<BlockState> targets;

	public DiskFeatureConfig(BlockState blockState, int i, int j, List<BlockState> list) {
		this.state = blockState;
		this.radius = i;
		this.ySize = j;
		this.targets = list;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("state"),
					BlockState.serialize(dynamicOps, this.state).getValue(),
					dynamicOps.createString("radius"),
					dynamicOps.createInt(this.radius),
					dynamicOps.createString("y_size"),
					dynamicOps.createInt(this.ySize),
					dynamicOps.createString("targets"),
					dynamicOps.createList(this.targets.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()))
				)
			)
		);
	}

	public static <T> DiskFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		int i = dynamic.get("radius").asInt(0);
		int j = dynamic.get("y_size").asInt(0);
		List<BlockState> list = dynamic.get("targets").asList(BlockState::deserialize);
		return new DiskFeatureConfig(blockState, i, j, list);
	}
}
