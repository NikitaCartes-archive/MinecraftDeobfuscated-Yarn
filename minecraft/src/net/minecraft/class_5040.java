package net.minecraft;

import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ColoredBlockArrays;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
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
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5040 extends RandomDimension {
	private static final boolean[] field_23525 = new boolean[]{
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		true,
		true,
		false,
		false,
		true,
		true,
		false,
		false,
		true,
		true,
		false,
		false,
		true,
		true,
		false,
		false,
		false,
		false,
		true,
		true,
		false,
		false,
		false,
		false,
		false,
		false,
		true,
		true,
		false,
		false,
		false,
		false,
		false,
		true,
		true,
		true,
		true,
		false,
		false,
		false,
		false,
		true,
		false,
		false,
		true,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false,
		false
	};

	public class_5040(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5040.class_5041(this.field_23566, method_26572(Biomes.JUNGLE), class_5099.field_23565);
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

	public static class class_5041 extends ChunkGenerator<class_5099> {
		private final OctaveSimplexNoiseSampler field_23526;
		private final OctaveSimplexNoiseSampler field_23527;
		private final ChunkRandom field_23528 = new ChunkRandom();

		public class_5041(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
			this.field_23526 = new OctaveSimplexNoiseSampler(this.field_23528, IntStream.rangeClosed(-5, 0));
			this.field_23527 = new OctaveSimplexNoiseSampler(this.field_23528, IntStream.rangeClosed(-5, 0));
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
			this.method_26546(chunk, 0, 0);
			this.method_26546(chunk, 0, 8);
			this.method_26546(chunk, 8, 0);
			this.method_26546(chunk, 8, 8);
		}

		private void method_26546(Chunk chunk, int i, int j) {
			ChunkPos chunkPos = chunk.getPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			double d = (1.0 + this.field_23526.sample((double)chunkPos.x, (double)chunkPos.z, false)) * 16.0;
			double e = (1.0 + this.field_23527.sample((double)chunkPos.x, (double)chunkPos.z, false)) * 16.0;
			this.field_23528.setTerrainSeed(2 * chunkPos.x + i, 2 * chunkPos.z + j);
			Block[] blocks = Util.method_26721(this.field_23528, ColoredBlockArrays.ALL);
			BlockState blockState = Util.method_26721(this.field_23528, blocks).getDefaultState();
			BlockState blockState2 = Util.method_26721(this.field_23528, blocks).getDefaultState();

			for (int k = 0; k < 8; k++) {
				for (int l = 0; l < 8; l++) {
					boolean bl = class_5040.field_23525[k * 8 + l];
					BlockState blockState3 = bl ? blockState : blockState2;
					double f = bl ? d : e;

					for (int m = 1; (double)m < f; m++) {
						chunk.setBlockState(mutable.set(k + i, m, l + j), blockState3, false);
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
			return ChunkGeneratorType.field_23465;
		}
	}
}
