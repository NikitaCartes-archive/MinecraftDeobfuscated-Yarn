package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EndPortalFeature extends Feature<DefaultFeatureConfig> {
	public static final BlockPos ORIGIN = BlockPos.ORIGIN;
	private final boolean open;

	public EndPortalFeature(boolean open) {
		super(DefaultFeatureConfig.CODEC);
		this.open = open;
	}

	public boolean method_13163(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (BlockPos blockPos2 : BlockPos.iterate(
			new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4)
		)) {
			boolean bl = blockPos2.isWithinDistance(blockPos, 2.5);
			if (bl || blockPos2.isWithinDistance(blockPos, 3.5)) {
				if (blockPos2.getY() < blockPos.getY()) {
					if (bl) {
						this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_9987.getDefaultState());
					} else if (blockPos2.getY() < blockPos.getY()) {
						this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_10471.getDefaultState());
					}
				} else if (blockPos2.getY() > blockPos.getY()) {
					this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_10124.getDefaultState());
				} else if (!bl) {
					this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_9987.getDefaultState());
				} else if (this.open) {
					this.setBlockState(structureWorldAccess, new BlockPos(blockPos2), Blocks.field_10027.getDefaultState());
				} else {
					this.setBlockState(structureWorldAccess, new BlockPos(blockPos2), Blocks.field_10124.getDefaultState());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.setBlockState(structureWorldAccess, blockPos.up(i), Blocks.field_9987.getDefaultState());
		}

		BlockPos blockPos3 = blockPos.up(2);

		for (Direction direction : Direction.Type.field_11062) {
			this.setBlockState(structureWorldAccess, blockPos3.offset(direction), Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, direction));
		}

		return true;
	}
}
