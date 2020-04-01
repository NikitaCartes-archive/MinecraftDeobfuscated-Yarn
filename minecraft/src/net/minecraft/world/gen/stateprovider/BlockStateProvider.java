package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.class_5107;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.ColoredBlockArrays;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public abstract class BlockStateProvider implements DynamicSerializable {
	protected final BlockStateProviderType<?> stateProvider;
	public static final List<BlockState> field_23592 = (List<BlockState>)Registry.BLOCK
		.stream()
		.map(Block::getDefaultState)
		.filter(blockState -> blockState.contains(PillarBlock.AXIS))
		.collect(ImmutableList.toImmutableList());

	protected BlockStateProvider(BlockStateProviderType<?> stateProvider) {
		this.stateProvider = stateProvider;
	}

	public abstract BlockState getBlockState(Random random, BlockPos pos);

	public static BlockStateProvider method_26659(Random random) {
		if (random.nextInt(20) != 0) {
			if (random.nextBoolean()) {
				return random.nextInt(5) == 0
					? new SimpleBlockStateProvider(Blocks.AIR.getDefaultState())
					: new SimpleBlockStateProvider(Util.method_26719(random, OverworldChunkGeneratorConfig.field_23567));
			} else if (random.nextBoolean()) {
				WeightedBlockStateProvider weightedBlockStateProvider = new WeightedBlockStateProvider();
				Util.method_26717(random, 1, 5, OverworldChunkGeneratorConfig.field_23567)
					.forEach(blockState -> weightedBlockStateProvider.addState(blockState, random.nextInt(5)));
				return weightedBlockStateProvider;
			} else {
				return new class_5107(
					(List<BlockState>)Stream.of(Util.method_26721(random, ColoredBlockArrays.ALL)).map(Block::getDefaultState).collect(ImmutableList.toImmutableList())
				);
			}
		} else {
			return new PillarBlockStateProvider(((BlockState)field_23592.get(random.nextInt(field_23592.size()))).getBlock());
		}
	}

	public static BlockStateProvider method_26660(Random random, List<BlockState> list) {
		if (random.nextBoolean()) {
			return new SimpleBlockStateProvider(Util.method_26719(random, list));
		} else {
			WeightedBlockStateProvider weightedBlockStateProvider = new WeightedBlockStateProvider();
			Util.method_26717(random, 1, 5, list).forEach(blockState -> weightedBlockStateProvider.addState(blockState, random.nextInt(5)));
			return weightedBlockStateProvider;
		}
	}
}
