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
	private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.field_10102);
	private final BlockState slab = Blocks.field_10007.method_9564();
	private final BlockState wall = Blocks.field_9979.method_9564();
	private final BlockState fluidInside = Blocks.field_10382.method_9564();

	public DesertWellFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12977(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		blockPos = blockPos.up();

		while (iWorld.method_8623(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!CAN_GENERATE.method_11760(iWorld.method_8320(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (iWorld.method_8623(blockPos.add(i, -1, j)) && iWorld.method_8623(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int i = -1; i <= 0; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					for (int k = -2; k <= 2; k++) {
						iWorld.method_8652(blockPos.add(jx, i, k), this.wall, 2);
					}
				}
			}

			iWorld.method_8652(blockPos, this.fluidInside, 2);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				iWorld.method_8652(blockPos.method_10093(direction), this.fluidInside, 2);
			}

			for (int i = -2; i <= 2; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					if (i == -2 || i == 2 || jx == -2 || jx == 2) {
						iWorld.method_8652(blockPos.add(i, 1, jx), this.wall, 2);
					}
				}
			}

			iWorld.method_8652(blockPos.add(2, 1, 0), this.slab, 2);
			iWorld.method_8652(blockPos.add(-2, 1, 0), this.slab, 2);
			iWorld.method_8652(blockPos.add(0, 1, 2), this.slab, 2);
			iWorld.method_8652(blockPos.add(0, 1, -2), this.slab, 2);

			for (int i = -1; i <= 1; i++) {
				for (int jxx = -1; jxx <= 1; jxx++) {
					if (i == 0 && jxx == 0) {
						iWorld.method_8652(blockPos.add(i, 4, jxx), this.wall, 2);
					} else {
						iWorld.method_8652(blockPos.add(i, 4, jxx), this.slab, 2);
					}
				}
			}

			for (int i = 1; i <= 3; i++) {
				iWorld.method_8652(blockPos.add(-1, i, -1), this.wall, 2);
				iWorld.method_8652(blockPos.add(-1, i, 1), this.wall, 2);
				iWorld.method_8652(blockPos.add(1, i, -1), this.wall, 2);
				iWorld.method_8652(blockPos.add(1, i, 1), this.wall, 2);
			}

			return true;
		}
	}
}
