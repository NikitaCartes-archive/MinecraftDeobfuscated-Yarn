package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class TwistingVinesFeature extends Feature<TwistingVinesFeatureConfig> {
	public TwistingVinesFeature(Codec<TwistingVinesFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<TwistingVinesFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		if (isNotSuitable(structureWorldAccess, blockPos)) {
			return false;
		} else {
			Random random = context.getRandom();
			TwistingVinesFeatureConfig twistingVinesFeatureConfig = context.getConfig();
			int i = twistingVinesFeatureConfig.spreadWidth();
			int j = twistingVinesFeatureConfig.spreadHeight();
			int k = twistingVinesFeatureConfig.maxHeight();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int l = 0; l < i * i; l++) {
				mutable.set(blockPos).move(MathHelper.nextInt(random, -i, i), MathHelper.nextInt(random, -j, j), MathHelper.nextInt(random, -i, i));
				if (canGenerate(structureWorldAccess, mutable) && !isNotSuitable(structureWorldAccess, mutable)) {
					int m = MathHelper.nextInt(random, 1, k);
					if (random.nextInt(6) == 0) {
						m *= 2;
					}

					if (random.nextInt(5) == 0) {
						m = 1;
					}

					int n = 17;
					int o = 25;
					generateVineColumn(structureWorldAccess, random, mutable, m, 17, 25);
				}
			}

			return true;
		}
	}

	private static boolean canGenerate(WorldAccess world, BlockPos.Mutable pos) {
		do {
			pos.move(0, -1, 0);
			if (world.isOutOfHeightLimit(pos)) {
				return false;
			}
		} while (world.getBlockState(pos).isAir());

		pos.move(0, 1, 0);
		return true;
	}

	public static void generateVineColumn(WorldAccess world, Random random, BlockPos.Mutable pos, int maxLength, int minAge, int maxAge) {
		for (int i = 1; i <= maxLength; i++) {
			if (world.isAir(pos)) {
				if (i == maxLength || !world.isAir(pos.up())) {
					world.setBlockState(
						pos,
						Blocks.TWISTING_VINES.getDefaultState().with(AbstractPlantStemBlock.AGE, Integer.valueOf(MathHelper.nextInt(random, minAge, maxAge))),
						Block.NOTIFY_LISTENERS
					);
					break;
				}

				world.setBlockState(pos, Blocks.TWISTING_VINES_PLANT.getDefaultState(), Block.NOTIFY_LISTENERS);
			}

			pos.move(Direction.UP);
		}
	}

	private static boolean isNotSuitable(WorldAccess world, BlockPos pos) {
		if (!world.isAir(pos)) {
			return true;
		} else {
			BlockState blockState = world.getBlockState(pos.down());
			return !blockState.isOf(Blocks.NETHERRACK) && !blockState.isOf(Blocks.WARPED_NYLIUM) && !blockState.isOf(Blocks.WARPED_WART_BLOCK);
		}
	}
}
