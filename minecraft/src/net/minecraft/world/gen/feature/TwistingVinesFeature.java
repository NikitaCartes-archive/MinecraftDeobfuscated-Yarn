package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class TwistingVinesFeature extends Feature<DefaultFeatureConfig> {
	public TwistingVinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld,
		StructureAccessor structureAccessor,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		return method_26265(iWorld, random, blockPos, 8, 4, 8);
	}

	public static boolean method_26265(IWorld iWorld, Random random, BlockPos blockPos, int i, int j, int k) {
		if (isNotSuitable(iWorld, blockPos)) {
			return false;
		} else {
			generateVinesInArea(iWorld, random, blockPos, i, j, k);
			return true;
		}
	}

	private static void generateVinesInArea(IWorld iWorld, Random random, BlockPos blockPos, int i, int j, int k) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = 0; l < i * i; l++) {
			mutable.set(blockPos).move(MathHelper.nextInt(random, -i, i), MathHelper.nextInt(random, -j, j), MathHelper.nextInt(random, -i, i));
			if (method_27220(iWorld, mutable) && !isNotSuitable(iWorld, mutable)) {
				int m = MathHelper.nextInt(random, 1, k);
				if (random.nextInt(6) == 0) {
					m *= 2;
				}

				if (random.nextInt(5) == 0) {
					m = 1;
				}

				int n = 17;
				int o = 25;
				generateVineColumn(iWorld, random, mutable, m, 17, 25);
			}
		}
	}

	private static boolean method_27220(IWorld iWorld, BlockPos.Mutable mutable) {
		do {
			mutable.move(0, -1, 0);
			if (World.isHeightInvalid(mutable)) {
				return false;
			}
		} while (iWorld.getBlockState(mutable).isAir());

		mutable.move(0, 1, 0);
		return true;
	}

	public static void generateVineColumn(IWorld world, Random random, BlockPos.Mutable pos, int maxLength, int minAge, int maxAge) {
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

	private static boolean isNotSuitable(IWorld iWorld, BlockPos blockPos) {
		if (!iWorld.isAir(blockPos)) {
			return true;
		} else {
			BlockState blockState = iWorld.getBlockState(blockPos.down());
			return !blockState.isOf(Blocks.NETHERRACK) && !blockState.isOf(Blocks.WARPED_NYLIUM) && !blockState.isOf(Blocks.WARPED_WART_BLOCK);
		}
	}
}
