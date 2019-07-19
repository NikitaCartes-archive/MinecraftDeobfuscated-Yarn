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

	public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, DefaultFeatureConfig config) {
		for (BlockPos blockPos : BlockPos.iterate(
			new BlockPos(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4), new BlockPos(pos.getX() + 4, pos.getY() + 32, pos.getZ() + 4)
		)) {
			boolean bl = blockPos.isWithinDistance(pos, 2.5);
			if (bl || blockPos.isWithinDistance(pos, 3.5)) {
				if (blockPos.getY() < pos.getY()) {
					if (bl) {
						this.setBlockState(world, blockPos, Blocks.BEDROCK.getDefaultState());
					} else if (blockPos.getY() < pos.getY()) {
						this.setBlockState(world, blockPos, Blocks.END_STONE.getDefaultState());
					}
				} else if (blockPos.getY() > pos.getY()) {
					this.setBlockState(world, blockPos, Blocks.AIR.getDefaultState());
				} else if (!bl) {
					this.setBlockState(world, blockPos, Blocks.BEDROCK.getDefaultState());
				} else if (this.open) {
					this.setBlockState(world, new BlockPos(blockPos), Blocks.END_PORTAL.getDefaultState());
				} else {
					this.setBlockState(world, new BlockPos(blockPos), Blocks.AIR.getDefaultState());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.setBlockState(world, pos.up(i), Blocks.BEDROCK.getDefaultState());
		}

		BlockPos blockPos2 = pos.up(2);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			this.setBlockState(world, blockPos2.offset(direction), Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, direction));
		}

		return true;
	}
}
