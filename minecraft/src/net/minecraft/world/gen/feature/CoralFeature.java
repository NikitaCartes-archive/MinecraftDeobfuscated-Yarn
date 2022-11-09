package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class CoralFeature extends Feature<DefaultFeatureConfig> {
	public CoralFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		Random random = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		Optional<Block> optional = Registries.BLOCK.getEntryList(BlockTags.CORAL_BLOCKS).flatMap(blocks -> blocks.getRandom(random)).map(RegistryEntry::value);
		return optional.isEmpty() ? false : this.generateCoral(structureWorldAccess, random, blockPos, ((Block)optional.get()).getDefaultState());
	}

	protected abstract boolean generateCoral(WorldAccess world, Random random, BlockPos pos, BlockState state);

	protected boolean generateCoralPiece(WorldAccess world, Random random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(pos);
		if ((blockState.isOf(Blocks.WATER) || blockState.isIn(BlockTags.CORALS)) && world.getBlockState(blockPos).isOf(Blocks.WATER)) {
			world.setBlockState(pos, state, Block.NOTIFY_ALL);
			if (random.nextFloat() < 0.25F) {
				Registries.BLOCK
					.getEntryList(BlockTags.CORALS)
					.flatMap(blocks -> blocks.getRandom(random))
					.map(RegistryEntry::value)
					.ifPresent(block -> world.setBlockState(blockPos, block.getDefaultState(), Block.NOTIFY_LISTENERS));
			} else if (random.nextFloat() < 0.05F) {
				world.setBlockState(
					blockPos, Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(random.nextInt(4) + 1)), Block.NOTIFY_LISTENERS
				);
			}

			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (random.nextFloat() < 0.2F) {
					BlockPos blockPos2 = pos.offset(direction);
					if (world.getBlockState(blockPos2).isOf(Blocks.WATER)) {
						Registries.BLOCK.getEntryList(BlockTags.WALL_CORALS).flatMap(blocks -> blocks.getRandom(random)).map(RegistryEntry::value).ifPresent(block -> {
							BlockState blockStatex = block.getDefaultState();
							if (blockStatex.contains(DeadCoralWallFanBlock.FACING)) {
								blockStatex = blockStatex.with(DeadCoralWallFanBlock.FACING, direction);
							}

							world.setBlockState(blockPos2, blockStatex, Block.NOTIFY_LISTENERS);
						});
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
