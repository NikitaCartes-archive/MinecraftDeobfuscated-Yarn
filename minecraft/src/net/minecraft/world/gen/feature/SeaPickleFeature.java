package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SeaPickleFeature extends Feature<CountConfig> {
	public SeaPickleFeature(Codec<CountConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<CountConfig> context) {
		int i = 0;
		AbstractRandom abstractRandom = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		int j = context.getConfig().getCount().get(abstractRandom);

		for (int k = 0; k < j; k++) {
			int l = abstractRandom.nextInt(8) - abstractRandom.nextInt(8);
			int m = abstractRandom.nextInt(8) - abstractRandom.nextInt(8);
			int n = structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + l, blockPos.getZ() + m);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + l, n, blockPos.getZ() + m);
			BlockState blockState = Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(abstractRandom.nextInt(4) + 1));
			if (structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER) && blockState.canPlaceAt(structureWorldAccess, blockPos2)) {
				structureWorldAccess.setBlockState(blockPos2, blockState, Block.NOTIFY_LISTENERS);
				i++;
			}
		}

		return i > 0;
	}
}
