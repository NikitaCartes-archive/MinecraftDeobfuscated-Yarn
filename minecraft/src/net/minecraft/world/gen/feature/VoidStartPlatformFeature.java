package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class VoidStartPlatformFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockPos field_19241 = new BlockPos(8, 3, 8);
	private static final ChunkPos field_19242 = new ChunkPos(field_19241);

	public VoidStartPlatformFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	private static int method_20403(int i, int j, int k, int l) {
		return Math.max(Math.abs(i - k), Math.abs(j - l));
	}

	public boolean method_14165(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (method_20403(chunkPos.x, chunkPos.z, field_19242.x, field_19242.z) > 1) {
			return true;
		} else {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = chunkPos.getStartZ(); i <= chunkPos.getEndZ(); i++) {
				for (int j = chunkPos.getStartX(); j <= chunkPos.getEndX(); j++) {
					if (method_20403(field_19241.getX(), field_19241.getZ(), j, i) <= 16) {
						mutable.set(j, field_19241.getY(), i);
						if (mutable.equals(field_19241)) {
							iWorld.setBlockState(mutable, Blocks.field_10445.getDefaultState(), 2);
						} else {
							iWorld.setBlockState(mutable, Blocks.field_10340.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
