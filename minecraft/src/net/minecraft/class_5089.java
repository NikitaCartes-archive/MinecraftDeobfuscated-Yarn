package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
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

public class class_5089 extends OverworldDimension {
	public class_5089(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return new class_5089.class_5090(this.world, RandomDimension.method_26573(this.world.getSeed()), ChunkGeneratorType.SURFACE.createConfig());
	}

	public static class class_5090 extends OverworldChunkGenerator {
		public class_5090(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
			super(iWorld, biomeSource, overworldChunkGeneratorConfig);
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			super.generateFeatures(region);
			ChunkRandom chunkRandom = new ChunkRandom();
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			chunkRandom.setTerrainSeed(i, j);
			if (chunkRandom.nextInt(10) == 0) {
				BlockState blockState = Blocks.AIR.getDefaultState();
				BlockState blockState2 = Blocks.OBSIDIAN.getDefaultState();
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				int k = 1 + chunkRandom.nextInt(10);
				int l = k * k + 1;
				int m = k + 3;
				int n = m * m + 1;

				for (int o = -m; o <= m; o++) {
					for (int p = -m; p <= m; p++) {
						int q = o * o + p * p;
						if (q <= l) {
							for (int r = 0; r < 256; r++) {
								region.setBlockState(mutable.set(16 * i + o, r, 16 * j + p), blockState2, 4);
							}
						} else if (q <= n) {
							for (int r = 0; r < 256; r++) {
								region.setBlockState(mutable.set(16 * i + o, r, 16 * j + p), blockState, 4);
							}
						}
					}
				}
			}
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23454;
		}
	}
}
