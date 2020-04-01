package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class DiskFeatureConfig implements FeatureConfig {
	public final BlockState state;
	public final int radius;
	public final int ySize;
	public final List<BlockState> targets;

	public DiskFeatureConfig(BlockState state, int radius, int ySize, List<BlockState> targets) {
		this.state = state;
		this.radius = radius;
		this.ySize = ySize;
		this.targets = targets;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("state"),
					BlockState.serialize(ops, this.state).getValue(),
					ops.createString("radius"),
					ops.createInt(this.radius),
					ops.createString("y_size"),
					ops.createInt(this.ySize),
					ops.createString("targets"),
					ops.createList(this.targets.stream().map(blockState -> BlockState.serialize(ops, blockState).getValue()))
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

	public static DiskFeatureConfig method_26609(Random random) {
		return new DiskFeatureConfig(
			Registry.BLOCK.getRandom(random).getDefaultState(),
			random.nextInt(20) + 2,
			random.nextInt(20) + 2,
			(List<BlockState>)Util.method_26718(random, 30, Registry.BLOCK).map(Block::getDefaultState).collect(Collectors.toList())
		);
	}
}
