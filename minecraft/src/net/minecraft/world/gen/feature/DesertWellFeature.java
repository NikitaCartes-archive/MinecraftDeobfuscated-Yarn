package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertWellFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
	private final BlockState slab = Blocks.SANDSTONE_SLAB.getDefaultState();
	private final BlockState wall = Blocks.SANDSTONE.getDefaultState();
	private final BlockState fluidInside = Blocks.WATER.getDefaultState();

	public DesertWellFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		blockPos = blockPos.up();

		while (serverWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!CAN_GENERATE.test(serverWorldAccess.getBlockState(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (serverWorldAccess.isAir(blockPos.add(i, -1, j)) && serverWorldAccess.isAir(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int i = -1; i <= 0; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					for (int k = -2; k <= 2; k++) {
						serverWorldAccess.setBlockState(blockPos.add(jx, i, k), this.wall, 2);
					}
				}
			}

			serverWorldAccess.setBlockState(blockPos, this.fluidInside, 2);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				serverWorldAccess.setBlockState(blockPos.offset(direction), this.fluidInside, 2);
			}

			for (int i = -2; i <= 2; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					if (i == -2 || i == 2 || jx == -2 || jx == 2) {
						serverWorldAccess.setBlockState(blockPos.add(i, 1, jx), this.wall, 2);
					}
				}
			}

			serverWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, 2);
			serverWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, 2);
			serverWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, 2);
			serverWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, 2);

			for (int i = -1; i <= 1; i++) {
				for (int jxx = -1; jxx <= 1; jxx++) {
					if (i == 0 && jxx == 0) {
						serverWorldAccess.setBlockState(blockPos.add(i, 4, jxx), this.wall, 2);
					} else {
						serverWorldAccess.setBlockState(blockPos.add(i, 4, jxx), this.slab, 2);
					}
				}
			}

			for (int i = 1; i <= 3; i++) {
				serverWorldAccess.setBlockState(blockPos.add(-1, i, -1), this.wall, 2);
				serverWorldAccess.setBlockState(blockPos.add(-1, i, 1), this.wall, 2);
				serverWorldAccess.setBlockState(blockPos.add(1, i, -1), this.wall, 2);
				serverWorldAccess.setBlockState(blockPos.add(1, i, 1), this.wall, 2);
			}

			return true;
		}
	}
}
