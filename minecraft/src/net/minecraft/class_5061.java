package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5061 extends RandomDimension {
	public class_5061(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.0F);
	}

	@Override
	public boolean hasVisibleSky() {
		return true;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5061.class_5062(this.field_23566, method_26573(this.field_23566.getSeed()), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return OverworldDimension.method_26524(timeOfDay, 24000.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d.multiply((double)(tickDelta * 0.94F + 0.06F), (double)(tickDelta * 0.94F + 0.06F), (double)(tickDelta * 0.91F + 0.09F));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	public static class class_5062 extends ChunkGenerator<class_5099> {
		public class_5062(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
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
			ChunkRandom chunkRandom = new ChunkRandom();
			ChunkPos chunkPos = chunk.getPos();
			chunkRandom.setTerrainSeed(chunkPos.x, chunkPos.z);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i += 4) {
				for (int j = 0; j < 256; j += 4) {
					for (int k = 0; k < 16; k += 4) {
						Block block = Registry.BLOCK.getRandom(chunkRandom);
						BlockState blockState = Util.method_26719(chunkRandom, block.getStateManager().getStates());
						chunk.setBlockState(mutable.set(i, j, k), blockState, false);
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
			return ChunkGeneratorType.field_23475;
		}
	}
}
