package net.minecraft;

import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class class_5055 extends RandomDimension {
	private static final Vec3d field_23539 = new Vec3d((double)method_26549(207.0F), (double)method_26549(104.0F), (double)method_26549(255.0F));

	private static float method_26549(float f) {
		return f / 255.0F;
	}

	public class_5055(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5055.class_5056(this.field_23566, method_26572(Biomes.JUNGLE), class_5099.field_23565);
	}

	@Override
	public boolean hasVisibleSky() {
		return true;
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.75F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return field_23539;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return true;
	}

	public static class class_5056 extends ChunkGenerator<class_5099> {
		private final OctaveSimplexNoiseSampler field_23540 = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), IntStream.rangeClosed(-5, 0));

		public class_5056(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
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
			BlockState blockState = Blocks.BLACK_CONCRETE.getDefaultState();
			BlockState blockState2 = Blocks.LIME_CONCRETE.getDefaultState();
			ChunkPos chunkPos = chunk.getPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					double d = 64.0
						+ (this.field_23540.sample((double)((float)chunkPos.x + (float)i / 16.0F), (double)((float)chunkPos.z + (float)j / 16.0F), false) + 1.0) * 20.0;

					for (int k = 0; (double)k < d; k++) {
						chunk.setBlockState(mutable.set(i, k, j), i != 0 && k % 16 != 0 && j != 0 ? blockState : blockState2, false);
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
			return ChunkGeneratorType.field_23472;
		}
	}
}
