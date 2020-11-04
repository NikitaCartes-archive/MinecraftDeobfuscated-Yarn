package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherrackReplaceBlobsFeature extends Feature<NetherrackReplaceBlobsFeatureConfig> {
	public NetherrackReplaceBlobsFeature(Codec<NetherrackReplaceBlobsFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		NetherrackReplaceBlobsFeatureConfig netherrackReplaceBlobsFeatureConfig
	) {
		Block block = netherrackReplaceBlobsFeatureConfig.target.getBlock();
		BlockPos blockPos2 = method_27107(
			structureWorldAccess,
			blockPos.mutableCopy().clamp(Direction.Axis.Y, structureWorldAccess.getBottomHeightLimit() + 1, structureWorldAccess.getTopHeightLimit() - 1),
			block
		);
		if (blockPos2 == null) {
			return false;
		} else {
			int i = netherrackReplaceBlobsFeatureConfig.getRadius().getValue(random);
			int j = netherrackReplaceBlobsFeatureConfig.getRadius().getValue(random);
			int k = netherrackReplaceBlobsFeatureConfig.getRadius().getValue(random);
			int l = Math.max(i, Math.max(j, k));
			boolean bl = false;

			for (BlockPos blockPos3 : BlockPos.iterateOutwards(blockPos2, i, j, k)) {
				if (blockPos3.getManhattanDistance(blockPos2) > l) {
					break;
				}

				BlockState blockState = structureWorldAccess.getBlockState(blockPos3);
				if (blockState.isOf(block)) {
					this.setBlockState(structureWorldAccess, blockPos3, netherrackReplaceBlobsFeatureConfig.state);
					bl = true;
				}
			}

			return bl;
		}
	}

	@Nullable
	private static BlockPos method_27107(WorldAccess worldAccess, BlockPos.Mutable mutable, Block block) {
		while (mutable.getY() > worldAccess.getBottomHeightLimit() + 1) {
			BlockState blockState = worldAccess.getBlockState(mutable);
			if (blockState.isOf(block)) {
				return mutable;
			}

			mutable.move(Direction.DOWN);
		}

		return null;
	}
}
