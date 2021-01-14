package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class TwistingVinesFeature extends Feature<DefaultFeatureConfig> {
	public TwistingVinesFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		return method_26265(structureWorldAccess, random, blockPos, 8, 4, 8);
	}

	public static boolean method_26265(WorldAccess worldAccess, Random random, BlockPos blockPos, int i, int j, int k) {
		if (isNotSuitable(worldAccess, blockPos)) {
			return false;
		} else {
			generateVinesInArea(worldAccess, random, blockPos, i, j, k);
			return true;
		}
	}

	private static void generateVinesInArea(WorldAccess world, Random random, BlockPos pos, int horizontalSpread, int verticalSpread, int length) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < horizontalSpread * horizontalSpread; i++) {
			mutable.set(pos)
				.move(
					MathHelper.nextInt(random, -horizontalSpread, horizontalSpread),
					MathHelper.nextInt(random, -verticalSpread, verticalSpread),
					MathHelper.nextInt(random, -horizontalSpread, horizontalSpread)
				);
			if (method_27220(world, mutable) && !isNotSuitable(world, mutable)) {
				int j = MathHelper.nextInt(random, 1, length);
				if (random.nextInt(6) == 0) {
					j *= 2;
				}

				if (random.nextInt(5) == 0) {
					j = 1;
				}

				int k = 17;
				int l = 25;
				generateVineColumn(world, random, mutable, j, 17, 25);
			}
		}
	}

	private static boolean method_27220(WorldAccess worldAccess, BlockPos.Mutable mutable) {
		do {
			mutable.move(0, -1, 0);
			if (World.isOutOfBuildLimitVertically(mutable)) {
				return false;
			}
		} while (worldAccess.getBlockState(mutable).isAir());

		mutable.move(0, 1, 0);
		return true;
	}

	public static void generateVineColumn(WorldAccess world, Random random, BlockPos.Mutable pos, int maxLength, int minAge, int maxAge) {
		for (int i = 1; i <= maxLength; i++) {
			if (world.isAir(pos)) {
				if (i == maxLength || !world.isAir(pos.up())) {
					world.setBlockState(
						pos, Blocks.TWISTING_VINES.getDefaultState().with(AbstractPlantStemBlock.AGE, Integer.valueOf(MathHelper.nextInt(random, minAge, maxAge))), 2
					);
					break;
				}

				world.setBlockState(pos, Blocks.TWISTING_VINES_PLANT.getDefaultState(), 2);
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
