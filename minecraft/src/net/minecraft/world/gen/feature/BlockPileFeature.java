package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BlockPileFeature extends Feature<BlockPileFeatureConfig> {
	public BlockPileFeature(Codec<BlockPileFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<BlockPileFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		AbstractRandom abstractRandom = context.getRandom();
		BlockPileFeatureConfig blockPileFeatureConfig = context.getConfig();
		if (blockPos.getY() < structureWorldAccess.getBottomY() + 5) {
			return false;
		} else {
			int i = 2 + abstractRandom.nextInt(2);
			int j = 2 + abstractRandom.nextInt(2);

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, 0, -j), blockPos.add(i, 1, j))) {
				int k = blockPos.getX() - blockPos2.getX();
				int l = blockPos.getZ() - blockPos2.getZ();
				if ((float)(k * k + l * l) <= abstractRandom.nextFloat() * 10.0F - abstractRandom.nextFloat() * 6.0F) {
					this.addPileBlock(structureWorldAccess, blockPos2, abstractRandom, blockPileFeatureConfig);
				} else if ((double)abstractRandom.nextFloat() < 0.031) {
					this.addPileBlock(structureWorldAccess, blockPos2, abstractRandom, blockPileFeatureConfig);
				}
			}

			return true;
		}
	}

	private boolean canPlace(WorldAccess world, BlockPos pos, AbstractRandom random) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isOf(Blocks.DIRT_PATH) ? random.nextBoolean() : blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
	}

	private void addPileBlock(WorldAccess world, BlockPos pos, AbstractRandom random, BlockPileFeatureConfig config) {
		if (world.isAir(pos) && this.canPlace(world, pos, random)) {
			world.setBlockState(pos, config.stateProvider.getBlockState(random, pos), Block.NO_REDRAW);
		}
	}
}
