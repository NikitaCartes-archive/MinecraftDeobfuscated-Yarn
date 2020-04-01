package net.minecraft;

import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
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

public class class_5093 extends OverworldDimension {
	public class_5093(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return new class_5093.class_5094(this.world, RandomDimension.method_26573(this.world.getSeed()), ChunkGeneratorType.SURFACE.createConfig());
	}

	public static class class_5094 extends OverworldChunkGenerator {
		private final OctaveSimplexNoiseSampler field_23557;

		public class_5094(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
			super(iWorld, biomeSource, overworldChunkGeneratorConfig);
			ChunkRandom chunkRandom = new ChunkRandom(iWorld.getSeed());
			this.field_23557 = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-4, 1));
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			super.generateFeatures(region);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			BlockState blockState = Blocks.ZONE.getDefaultState();

			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					int m = 16 * i + k;
					int n = 16 * j + l;
					int o = region.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, m, n);
					int p = (int)(this.field_23557.sample((double)((float)m / 16.0F), (double)((float)n / 16.0F), false) * (double)o / 3.0) - 1;
					if (p > 0) {
						for (int q = -p; q < p; q++) {
							mutable.set(m, o + q, n);
							if (region.isAir(mutable)) {
								region.setBlockState(mutable, blockState, 4);
							}
						}
					}
				}
			}
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23456;
		}
	}
}
