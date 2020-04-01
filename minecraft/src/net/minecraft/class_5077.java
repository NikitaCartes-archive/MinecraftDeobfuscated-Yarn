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
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5077 extends RandomDimension {
	public class_5077(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5077.class_5078(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return OverworldDimension.method_26524(timeOfDay, 24000.0);
	}

	@Override
	public boolean hasVisibleSky() {
		return true;
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

	public static class class_5078 extends ChunkGenerator<class_5099> {
		public class_5078(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
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
			BlockState blockState = Blocks.GRASS_BLOCK.getDefaultState();
			BlockState blockState2 = Blocks.MOSSY_COBBLESTONE.getDefaultState();
			ChunkPos chunkPos = chunk.getPos();
			int i = chunkPos.getStartX();
			int j = chunkPos.getStartZ();
			int k = 0;
			int l;
			if (chunkPos.x >= 0) {
				l = 0;
			} else if (chunkPos.z >= 0) {
				l = -1;
			} else {
				l = 1;
			}

			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < 16; n++) {
					int o = Math.max(Math.abs(i + m - l), Math.abs(j + n - 0));
					if (o % 2 == 0) {
						chunk.setBlockState(mutable.set(m, 50, n), blockState2, false);
						chunk.setBlockState(mutable.move(0, 1, 0), blockState2, false);
						chunk.setBlockState(mutable.move(0, 1, 0), blockState2, false);
					} else {
						chunk.setBlockState(mutable.set(m, 50, n), blockState, false);
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
			return ChunkGeneratorType.field_23448;
		}
	}
}
