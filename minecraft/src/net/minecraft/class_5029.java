package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5029 extends RandomDimension {
	public class_5029(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5029.class_5030(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 12000.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	public static class class_5030 extends ChunkGenerator<class_5099> {
		public class_5030(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public int getSpawnHeight() {
			return 30;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
			ChunkPos chunkPos = chunk.getPos();
			if (chunkPos.x >= 0 && chunkPos.z >= 0) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 256; j++) {
						for (int k = 0; k < 16; k++) {
							int l = chunkPos.getStartX() + i;
							int m = chunkPos.getStartZ() + k;
							if (method_26533(l, j, m)) {
								chunk.setBlockState(mutable.set(l, j, m), Blocks.SPONGE.getDefaultState(), false);
							}
						}
					}
				}
			}
		}

		private static boolean method_26533(int i, int j, int k) {
			while (i != 0 || j != 0 || k != 0) {
				int l = i % 3 == 1 ? 1 : 0;
				int m = j % 3 == 1 ? 1 : 0;
				int n = k % 3 == 1 ? 1 : 0;
				if (l + m + n >= 2) {
					return false;
				}

				i /= 3;
				j /= 3;
				k /= 3;
			}

			return true;
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23462;
		}
	}
}
