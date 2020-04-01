package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class class_5075 extends OverworldDimension {
	public class_5075(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return new class_5075.class_5076(this.world, RandomDimension.method_26573(this.world.getSeed()), ChunkGeneratorType.SURFACE.createConfig());
	}

	public static class class_5076 extends OverworldChunkGenerator {
		public class_5076(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
			super(iWorld, biomeSource, overworldChunkGeneratorConfig);
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			super.generateFeatures(region);
			BlockState blockState = Blocks.SLIME_BLOCK.getDefaultState();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					int k = region.getCenterChunkX() * 16 + i;
					int l = region.getCenterChunkZ() * 16 + j;
					int m = region.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l);

					for (int n = 0; n < 10; n++) {
						region.setBlockState(mutable.set(k, m + n, l), blockState, 4);
					}
				}
			}
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23447;
		}
	}
}
