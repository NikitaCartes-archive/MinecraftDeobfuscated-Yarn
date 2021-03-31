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
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ReplaceBlobsFeature extends Feature<ReplaceBlobsFeatureConfig> {
	public ReplaceBlobsFeature(Codec<ReplaceBlobsFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<ReplaceBlobsFeatureConfig> context) {
		ReplaceBlobsFeatureConfig replaceBlobsFeatureConfig = context.getConfig();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		Block block = replaceBlobsFeatureConfig.target.getBlock();
		BlockPos blockPos = moveDownToTarget(
			structureWorldAccess,
			context.getOrigin().mutableCopy().clamp(Direction.Axis.Y, structureWorldAccess.getBottomY() + 1, structureWorldAccess.getTopY() - 1),
			block
		);
		if (blockPos == null) {
			return false;
		} else {
			int i = replaceBlobsFeatureConfig.getRadius().get(random);
			int j = replaceBlobsFeatureConfig.getRadius().get(random);
			int k = replaceBlobsFeatureConfig.getRadius().get(random);
			int l = Math.max(i, Math.max(j, k));
			boolean bl = false;

			for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, i, j, k)) {
				if (blockPos2.getManhattanDistance(blockPos) > l) {
					break;
				}

				BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
				if (blockState.isOf(block)) {
					this.setBlockState(structureWorldAccess, blockPos2, replaceBlobsFeatureConfig.state);
					bl = true;
				}
			}

			return bl;
		}
	}

	@Nullable
	private static BlockPos moveDownToTarget(WorldAccess world, BlockPos.Mutable mutablePos, Block target) {
		while (mutablePos.getY() > world.getBottomY() + 1) {
			BlockState blockState = world.getBlockState(mutablePos);
			if (blockState.isOf(target)) {
				return mutablePos;
			}

			mutablePos.move(Direction.DOWN);
		}

		return null;
	}
}
