package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class TwistingVinesFeature extends Feature<DefaultFeatureConfig> {
	public TwistingVinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (this.isNotSuitable(iWorld, blockPos)) {
			return false;
		} else {
			this.generateVinesInArea(iWorld, random, blockPos);
			return true;
		}
	}

	private void generateVinesInArea(IWorld world, Random random, BlockPos pos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < 100; i++) {
			mutable.set(pos).move(MathHelper.nextInt(random, -8, 8), MathHelper.nextInt(random, -2, 7), MathHelper.nextInt(random, -8, 8));

			while (world.getBlockState(mutable.down()).isAir()) {
				mutable.move(0, -1, 0);
			}

			if (!this.isNotSuitable(world, mutable)) {
				int j = MathHelper.nextInt(random, 1, 8);
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

	public static void generateVineColumn(IWorld world, Random random, BlockPos.Mutable pos, int maxLength, int minAge, int maxAge) {
		for (int i = 0; i <= maxLength; i++) {
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

	private boolean isNotSuitable(IWorld world, BlockPos pos) {
		if (!world.isAir(pos)) {
			return true;
		} else {
			Block block = world.getBlockState(pos.down()).getBlock();
			return block != Blocks.NETHERRACK && block != Blocks.WARPED_NYLIUM && block != Blocks.WARPED_WART_BLOCK;
		}
	}
}
