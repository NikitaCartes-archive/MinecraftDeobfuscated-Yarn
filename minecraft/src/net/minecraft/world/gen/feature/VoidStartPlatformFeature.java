package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class VoidStartPlatformFeature extends Feature<DefaultFeatureConfig> {
	public VoidStartPlatformFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_14165(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos blockPos2 = iWorld.method_8395();
		int i = 16;
		double d = blockPos2.squaredDistanceTo(blockPos.add(8, blockPos2.getY(), 8));
		if (d > 1024.0) {
			return true;
		} else {
			BlockPos blockPos3 = new BlockPos(blockPos2.getX() - 16, Math.max(blockPos2.getY(), 4) - 1, blockPos2.getZ() - 16);
			BlockPos blockPos4 = new BlockPos(blockPos2.getX() + 16, Math.max(blockPos2.getY(), 4) - 1, blockPos2.getZ() + 16);
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos3);

			for (int j = blockPos.getZ(); j < blockPos.getZ() + 16; j++) {
				for (int k = blockPos.getX(); k < blockPos.getX() + 16; k++) {
					if (j >= blockPos3.getZ() && j <= blockPos4.getZ() && k >= blockPos3.getX() && k <= blockPos4.getX()) {
						mutable.set(k, mutable.getY(), j);
						if (blockPos2.getX() == k && blockPos2.getZ() == j) {
							iWorld.method_8652(mutable, Blocks.field_10445.method_9564(), 2);
						} else {
							iWorld.method_8652(mutable, Blocks.field_10340.method_9564(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
