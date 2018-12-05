package net.minecraft;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class class_3033 extends Feature<DefaultFeatureConfig> {
	public static final BlockPos field_13600 = BlockPos.ORIGIN;
	private final boolean field_13599;

	public class_3033(boolean bl) {
		super(DefaultFeatureConfig::deserialize);
		this.field_13599 = bl;
	}

	public boolean method_13163(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(
			new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4)
		)) {
			double d = mutable.distanceTo(blockPos.getX(), mutable.getY(), blockPos.getZ());
			if (d <= 3.5) {
				if (mutable.getY() < blockPos.getY()) {
					if (d <= 2.5) {
						this.method_13153(iWorld, mutable, Blocks.field_9987.getDefaultState());
					} else if (mutable.getY() < blockPos.getY()) {
						this.method_13153(iWorld, mutable, Blocks.field_10471.getDefaultState());
					}
				} else if (mutable.getY() > blockPos.getY()) {
					this.method_13153(iWorld, mutable, Blocks.field_10124.getDefaultState());
				} else if (d > 2.5) {
					this.method_13153(iWorld, mutable, Blocks.field_9987.getDefaultState());
				} else if (this.field_13599) {
					this.method_13153(iWorld, new BlockPos(mutable), Blocks.field_10027.getDefaultState());
				} else {
					this.method_13153(iWorld, new BlockPos(mutable), Blocks.field_10124.getDefaultState());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.method_13153(iWorld, blockPos.up(i), Blocks.field_9987.getDefaultState());
		}

		BlockPos blockPos2 = blockPos.up(2);

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			this.method_13153(iWorld, blockPos2.method_10093(direction), Blocks.field_10099.getDefaultState().with(WallTorchBlock.field_11731, direction));
		}

		return true;
	}
}
