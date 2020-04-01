package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

public class class_5091 extends OverworldDimension {
	public class_5091(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return new class_5091.class_5092(this.world, RandomDimension.method_26573(this.world.getSeed()), ChunkGeneratorType.SURFACE.createConfig());
	}

	public static class class_5092 extends OverworldChunkGenerator {
		public class_5092(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
			super(iWorld, biomeSource, overworldChunkGeneratorConfig);
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			super.generateFeatures(region);
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setTerrainSeed(i, j);
			int k = i * i + j * j;
			int l = Math.min(MathHelper.floor(Math.sqrt((double)k) / 3.0 + 1.0), 16);
			int m = chunkRandom.nextInt(Math.min(k / 2 + 1, 32768));
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockPos.Mutable mutable2 = new BlockPos.Mutable();

			for (int n = 0; n < m; n++) {
				int o = 16 * i + chunkRandom.nextInt(16);
				int p = 16 * j + chunkRandom.nextInt(16);
				int q = region.getTopY(Heightmap.Type.MOTION_BLOCKING, o, p);
				int r = chunkRandom.nextInt(q + 5);
				mutable.set(o, r, p);
				mutable2.set(mutable, chunkRandom.nextInt(l), chunkRandom.nextInt(l), chunkRandom.nextInt(l));
				BlockState blockState = region.getBlockState(mutable);
				region.setBlockState(mutable, region.getBlockState(mutable2), 4);
				region.setBlockState(mutable2, blockState, 4);
			}
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23455;
		}
	}
}
