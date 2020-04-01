package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
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

public class class_5069 extends RandomDimension {
	public class_5069(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5069.class_5070(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
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

	public static class class_5070 extends ChunkGenerator<class_5099> {
		public class_5070(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public int getSpawnHeight() {
			return 100;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
			ChunkPos chunkPos = chunk.getPos();
			this.method_26552(chunk, chunkPos, 0, 0);
			this.method_26552(chunk, chunkPos, 1, 0);
			this.method_26552(chunk, chunkPos, 0, 1);
			this.method_26552(chunk, chunkPos, 1, 1);
		}

		private void method_26552(Chunk chunk, ChunkPos chunkPos, int i, int j) {
			int k = 8388607;
			long l = ((long)chunkPos.x * 2L + (long)i & 8388607L) << 31 | ((long)chunkPos.z * 2L + (long)j & 8388607L) << 8;
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockState blockState = Blocks.WHITE_CONCRETE.getDefaultState();
			BlockState blockState2 = Blocks.BLACK_CONCRETE.getDefaultState();

			for (int m = 0; m < 256; m++) {
				long n = l | (long)m;

				for (int o = 0; o < 7; o++) {
					for (int p = 0; p < 7; p++) {
						boolean bl = (n & 1L) != 0L;
						n >>= 1;
						chunk.setBlockState(mutable.set(o + i * 8, m, p + j * 8), bl ? blockState : blockState2, false);
					}
				}
			}
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 100;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23444;
		}
	}
}
