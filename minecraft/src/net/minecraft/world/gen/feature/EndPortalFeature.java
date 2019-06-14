package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class EndPortalFeature extends Feature<DefaultFeatureConfig> {
	public static final BlockPos ORIGIN = BlockPos.ORIGIN;
	private final boolean open;

	public EndPortalFeature(boolean bl) {
		super(DefaultFeatureConfig::deserialize);
		this.open = bl;
	}

	public boolean method_13163(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (BlockPos blockPos2 : BlockPos.iterate(
			new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4)
		)) {
			boolean bl = blockPos2.isWithinDistance(blockPos, 2.5);
			if (bl || blockPos2.isWithinDistance(blockPos, 3.5)) {
				if (blockPos2.getY() < blockPos.getY()) {
					if (bl) {
						this.setBlockState(iWorld, blockPos2, Blocks.field_9987.method_9564());
					} else if (blockPos2.getY() < blockPos.getY()) {
						this.setBlockState(iWorld, blockPos2, Blocks.field_10471.method_9564());
					}
				} else if (blockPos2.getY() > blockPos.getY()) {
					this.setBlockState(iWorld, blockPos2, Blocks.field_10124.method_9564());
				} else if (!bl) {
					this.setBlockState(iWorld, blockPos2, Blocks.field_9987.method_9564());
				} else if (this.open) {
					this.setBlockState(iWorld, new BlockPos(blockPos2), Blocks.field_10027.method_9564());
				} else {
					this.setBlockState(iWorld, new BlockPos(blockPos2), Blocks.field_10124.method_9564());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.setBlockState(iWorld, blockPos.up(i), Blocks.field_9987.method_9564());
		}

		BlockPos blockPos3 = blockPos.up(2);

		for (Direction direction : Direction.Type.field_11062) {
			this.setBlockState(iWorld, blockPos3.offset(direction), Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, direction));
		}

		return true;
	}
}
