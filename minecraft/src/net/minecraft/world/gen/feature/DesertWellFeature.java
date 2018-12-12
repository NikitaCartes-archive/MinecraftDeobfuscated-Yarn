package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class DesertWellFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockStatePredicate field_13450 = BlockStatePredicate.forBlock(Blocks.field_10102);
	private final BlockState field_13452 = Blocks.field_10007.getDefaultState();
	private final BlockState field_13451 = Blocks.field_9979.getDefaultState();
	private final BlockState field_13449 = Blocks.field_10382.getDefaultState();

	public DesertWellFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12977(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		blockPos = blockPos.up();

		while (iWorld.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!field_13450.apply(iWorld.getBlockState(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (iWorld.isAir(blockPos.add(i, -1, j)) && iWorld.isAir(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int i = -1; i <= 0; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					for (int k = -2; k <= 2; k++) {
						iWorld.setBlockState(blockPos.add(jx, i, k), this.field_13451, 2);
					}
				}
			}

			iWorld.setBlockState(blockPos, this.field_13449, 2);

			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				iWorld.setBlockState(blockPos.offset(direction), this.field_13449, 2);
			}

			for (int i = -2; i <= 2; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					if (i == -2 || i == 2 || jx == -2 || jx == 2) {
						iWorld.setBlockState(blockPos.add(i, 1, jx), this.field_13451, 2);
					}
				}
			}

			iWorld.setBlockState(blockPos.add(2, 1, 0), this.field_13452, 2);
			iWorld.setBlockState(blockPos.add(-2, 1, 0), this.field_13452, 2);
			iWorld.setBlockState(blockPos.add(0, 1, 2), this.field_13452, 2);
			iWorld.setBlockState(blockPos.add(0, 1, -2), this.field_13452, 2);

			for (int i = -1; i <= 1; i++) {
				for (int jxx = -1; jxx <= 1; jxx++) {
					if (i == 0 && jxx == 0) {
						iWorld.setBlockState(blockPos.add(i, 4, jxx), this.field_13451, 2);
					} else {
						iWorld.setBlockState(blockPos.add(i, 4, jxx), this.field_13452, 2);
					}
				}
			}

			for (int i = 1; i <= 3; i++) {
				iWorld.setBlockState(blockPos.add(-1, i, -1), this.field_13451, 2);
				iWorld.setBlockState(blockPos.add(-1, i, 1), this.field_13451, 2);
				iWorld.setBlockState(blockPos.add(1, i, -1), this.field_13451, 2);
				iWorld.setBlockState(blockPos.add(1, i, 1), this.field_13451, 2);
			}

			return true;
		}
	}
}
