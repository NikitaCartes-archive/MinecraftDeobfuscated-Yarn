package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertWellFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.field_10102);
	private final BlockState slab = Blocks.field_10007.getDefaultState();
	private final BlockState wall = Blocks.field_9979.getDefaultState();
	private final BlockState fluidInside = Blocks.field_10382.getDefaultState();

	public DesertWellFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_12977(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		blockPos = blockPos.up();

		while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.method_10074();
		}

		if (!CAN_GENERATE.method_11760(structureWorldAccess.getBlockState(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (structureWorldAccess.isAir(blockPos.add(i, -1, j)) && structureWorldAccess.isAir(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int i = -1; i <= 0; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					for (int k = -2; k <= 2; k++) {
						structureWorldAccess.setBlockState(blockPos.add(jx, i, k), this.wall, 2);
					}
				}
			}

			structureWorldAccess.setBlockState(blockPos, this.fluidInside, 2);

			for (Direction direction : Direction.Type.field_11062) {
				structureWorldAccess.setBlockState(blockPos.offset(direction), this.fluidInside, 2);
			}

			for (int i = -2; i <= 2; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					if (i == -2 || i == 2 || jx == -2 || jx == 2) {
						structureWorldAccess.setBlockState(blockPos.add(i, 1, jx), this.wall, 2);
					}
				}
			}

			structureWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, 2);
			structureWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, 2);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, 2);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, 2);

			for (int i = -1; i <= 1; i++) {
				for (int jxx = -1; jxx <= 1; jxx++) {
					if (i == 0 && jxx == 0) {
						structureWorldAccess.setBlockState(blockPos.add(i, 4, jxx), this.wall, 2);
					} else {
						structureWorldAccess.setBlockState(blockPos.add(i, 4, jxx), this.slab, 2);
					}
				}
			}

			for (int i = 1; i <= 3; i++) {
				structureWorldAccess.setBlockState(blockPos.add(-1, i, -1), this.wall, 2);
				structureWorldAccess.setBlockState(blockPos.add(-1, i, 1), this.wall, 2);
				structureWorldAccess.setBlockState(blockPos.add(1, i, -1), this.wall, 2);
				structureWorldAccess.setBlockState(blockPos.add(1, i, 1), this.wall, 2);
			}

			return true;
		}
	}
}
