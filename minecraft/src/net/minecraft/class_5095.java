package net.minecraft;

import java.util.stream.IntStream;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class class_5095 extends OverworldDimension {
	public class_5095(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return new class_5095.class_5096(this.world, RandomDimension.method_26573(this.world.getSeed()), ChunkGeneratorType.SURFACE.createConfig());
	}

	public static class class_5096 extends OverworldChunkGenerator {
		public static final SimpleBlockStateProvider field_23558 = new SimpleBlockStateProvider(Blocks.AIR.getDefaultState());
		private final OctaveSimplexNoiseSampler field_23559;

		public class_5096(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
			super(iWorld, biomeSource, overworldChunkGeneratorConfig);
			ChunkRandom chunkRandom = new ChunkRandom(iWorld.getSeed());
			this.field_23559 = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-4, 1));
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			super.generateFeatures(region);
			ChunkRandom chunkRandom = new ChunkRandom();
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			chunkRandom.setTerrainSeed(i, j);
			int k = chunkRandom.nextInt(4);

			for (int l = 0; l < k; l++) {
				int m = 16 * i + chunkRandom.nextInt(16);
				int n = 16 * j + chunkRandom.nextInt(16);
				int o = region.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, m, n);
				int p = MathHelper.ceil((double)o * (1.0 + chunkRandom.nextGaussian() / 2.0));
				class_5105.class_5106 lv = Util.method_26721(chunkRandom, class_5105.class_5106.values());
				float f = 2.0F + chunkRandom.nextFloat() * 5.0F;
				float g = Math.min(f + chunkRandom.nextFloat() * 10.0F, 15.0F);
				Feature.SHAPE.generate(region, this, chunkRandom, new BlockPos(m, p, n), new class_5105(field_23558, lv, f, g));
			}
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23457;
		}
	}
}
