package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class VoidStartPlatformFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockPos START_BLOCK = new BlockPos(8, 3, 8);
	private static final ChunkPos START_CHUNK = new ChunkPos(START_BLOCK);
	private static final int MAX_RADIUS = 16;
	private static final int field_31521 = 1;

	public VoidStartPlatformFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	private static int getDistance(int x1, int z1, int x2, int z2) {
		return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		ChunkPos chunkPos = new ChunkPos(context.getOrigin());
		if (getDistance(chunkPos.x, chunkPos.z, START_CHUNK.x, START_CHUNK.z) > 1) {
			return true;
		} else {
			BlockPos blockPos = START_BLOCK.withY(context.getOrigin().getY() + START_BLOCK.getY());
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = chunkPos.getStartZ(); i <= chunkPos.getEndZ(); i++) {
				for (int j = chunkPos.getStartX(); j <= chunkPos.getEndX(); j++) {
					if (getDistance(blockPos.getX(), blockPos.getZ(), j, i) <= 16) {
						mutable.set(j, blockPos.getY(), i);
						if (mutable.equals(blockPos)) {
							structureWorldAccess.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
						} else {
							structureWorldAccess.setBlockState(mutable, Blocks.STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
						}
					}
				}
			}

			return true;
		}
	}
}
