package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DesertWellFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
	private final BlockState slab = Blocks.SANDSTONE_SLAB.getDefaultState();
	private final BlockState wall = Blocks.SANDSTONE.getDefaultState();
	private final BlockState fluidInside = Blocks.WATER.getDefaultState();

	public DesertWellFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		blockPos = blockPos.up();

		while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > structureWorldAccess.getBottomY() + 2) {
			blockPos = blockPos.down();
		}

		if (!CAN_GENERATE.test(structureWorldAccess.getBlockState(blockPos))) {
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
						structureWorldAccess.setBlockState(blockPos.add(jx, i, k), this.wall, Block.NOTIFY_LISTENERS);
					}
				}
			}

			structureWorldAccess.setBlockState(blockPos, this.fluidInside, Block.NOTIFY_LISTENERS);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				structureWorldAccess.setBlockState(blockPos.offset(direction), this.fluidInside, Block.NOTIFY_LISTENERS);
			}

			for (int i = -2; i <= 2; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					if (i == -2 || i == 2 || jx == -2 || jx == 2) {
						structureWorldAccess.setBlockState(blockPos.add(i, 1, jx), this.wall, Block.NOTIFY_LISTENERS);
					}
				}
			}

			structureWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
			structureWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, Block.NOTIFY_LISTENERS);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, Block.NOTIFY_LISTENERS);

			for (int i = -1; i <= 1; i++) {
				for (int jxx = -1; jxx <= 1; jxx++) {
					if (i == 0 && jxx == 0) {
						structureWorldAccess.setBlockState(blockPos.add(i, 4, jxx), this.wall, Block.NOTIFY_LISTENERS);
					} else {
						structureWorldAccess.setBlockState(blockPos.add(i, 4, jxx), this.slab, Block.NOTIFY_LISTENERS);
					}
				}
			}

			for (int i = 1; i <= 3; i++) {
				structureWorldAccess.setBlockState(blockPos.add(-1, i, -1), this.wall, Block.NOTIFY_LISTENERS);
				structureWorldAccess.setBlockState(blockPos.add(-1, i, 1), this.wall, Block.NOTIFY_LISTENERS);
				structureWorldAccess.setBlockState(blockPos.add(1, i, -1), this.wall, Block.NOTIFY_LISTENERS);
				structureWorldAccess.setBlockState(blockPos.add(1, i, 1), this.wall, Block.NOTIFY_LISTENERS);
			}

			return true;
		}
	}
}
