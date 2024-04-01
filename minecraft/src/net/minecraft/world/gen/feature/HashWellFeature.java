package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class HashWellFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.GRAVTATER);
	private final BlockState gravtater = Blocks.GRAVTATER.getDefaultState();
	private final BlockState slab = Blocks.CHARRED_BAKED_POTATO_BRICK_SLAB.getDefaultState();
	private final BlockState wall = Blocks.CHARRED_BAKED_POTATO_BRICKS.getDefaultState();
	private final BlockState fluidInside = Blocks.LIGHT
		.getDefaultState()
		.with(LightBlock.LEVEL_15, Integer.valueOf(15))
		.with(LightBlock.WATERLOGGED, Boolean.valueOf(true));

	public HashWellFeature(Codec<DefaultFeatureConfig> codec) {
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

			for (int i = -2; i <= 0; i++) {
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

			BlockPos blockPos2 = blockPos.down();
			structureWorldAccess.setBlockState(blockPos2, this.gravtater, Block.NOTIFY_LISTENERS);

			for (Direction direction2 : Direction.Type.HORIZONTAL) {
				structureWorldAccess.setBlockState(blockPos2.offset(direction2), this.gravtater, Block.NOTIFY_LISTENERS);
			}

			for (int jx = -2; jx <= 2; jx++) {
				for (int k = -2; k <= 2; k++) {
					if (jx == -2 || jx == 2 || k == -2 || k == 2) {
						structureWorldAccess.setBlockState(blockPos.add(jx, 1, k), this.wall, Block.NOTIFY_LISTENERS);
					}
				}
			}

			structureWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
			structureWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, Block.NOTIFY_LISTENERS);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, Block.NOTIFY_LISTENERS);

			for (int jx = -1; jx <= 1; jx++) {
				for (int kx = -1; kx <= 1; kx++) {
					if (jx == 0 && kx == 0) {
						structureWorldAccess.setBlockState(blockPos.add(jx, 4, kx), this.wall, Block.NOTIFY_LISTENERS);
					} else {
						structureWorldAccess.setBlockState(blockPos.add(jx, 4, kx), this.slab, Block.NOTIFY_LISTENERS);
					}
				}
			}

			for (int jx = 1; jx <= 3; jx++) {
				structureWorldAccess.setBlockState(blockPos.add(-1, jx, -1), this.wall, Block.NOTIFY_LISTENERS);
				structureWorldAccess.setBlockState(blockPos.add(-1, jx, 1), this.wall, Block.NOTIFY_LISTENERS);
				structureWorldAccess.setBlockState(blockPos.add(1, jx, -1), this.wall, Block.NOTIFY_LISTENERS);
				structureWorldAccess.setBlockState(blockPos.add(1, jx, 1), this.wall, Block.NOTIFY_LISTENERS);
			}

			return true;
		}
	}
}
