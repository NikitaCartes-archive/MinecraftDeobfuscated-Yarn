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

	public EndPortalFeature(boolean open) {
		super(DefaultFeatureConfig::deserialize);
		this.open = open;
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (BlockPos blockPos2 : BlockPos.iterate(
			new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4)
		)) {
			boolean bl = blockPos2.isWithinDistance(blockPos, 2.5);
			if (bl || blockPos2.isWithinDistance(blockPos, 3.5)) {
				if (blockPos2.getY() < blockPos.getY()) {
					if (bl) {
						this.setBlockState(iWorld, blockPos2, Blocks.BEDROCK.getDefaultState());
					} else if (blockPos2.getY() < blockPos.getY()) {
						this.setBlockState(iWorld, blockPos2, Blocks.END_STONE.getDefaultState());
					}
				} else if (blockPos2.getY() > blockPos.getY()) {
					this.setBlockState(iWorld, blockPos2, Blocks.AIR.getDefaultState());
				} else if (!bl) {
					this.setBlockState(iWorld, blockPos2, Blocks.BEDROCK.getDefaultState());
				} else if (this.open) {
					this.setBlockState(iWorld, new BlockPos(blockPos2), Blocks.END_PORTAL.getDefaultState());
				} else {
					this.setBlockState(iWorld, new BlockPos(blockPos2), Blocks.AIR.getDefaultState());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.setBlockState(iWorld, blockPos.up(i), Blocks.BEDROCK.getDefaultState());
		}

		BlockPos blockPos3 = blockPos.up(2);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			this.setBlockState(iWorld, blockPos3.offset(direction), Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, direction));
		}

		return true;
	}
}
