package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DiskFeature extends Feature<DiskFeatureConfig> {
	public DiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DiskFeatureConfig diskFeatureConfig
	) {
		boolean bl = false;
		int i = diskFeatureConfig.radius.method_30321(random);

		for (int j = blockPos.getX() - i; j <= blockPos.getX() + i; j++) {
			for (int k = blockPos.getZ() - i; k <= blockPos.getZ() + i; k++) {
				int l = j - blockPos.getX();
				int m = k - blockPos.getZ();
				if (l * l + m * m <= i * i) {
					for (int n = blockPos.getY() - diskFeatureConfig.ySize; n <= blockPos.getY() + diskFeatureConfig.ySize; n++) {
						BlockPos blockPos2 = new BlockPos(j, n, k);
						Block block = serverWorldAccess.getBlockState(blockPos2).getBlock();

						for (BlockState blockState : diskFeatureConfig.targets) {
							if (blockState.isOf(block)) {
								serverWorldAccess.setBlockState(blockPos2, diskFeatureConfig.state, 2);
								bl = true;
								break;
							}
						}
					}
				}
			}
		}

		return bl;
	}
}
