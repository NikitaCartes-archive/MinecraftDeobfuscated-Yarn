package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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

public class class_5047 extends RandomDimension {
	public class_5047(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5047.class_5048(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
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

	public static class class_5048 extends ChunkGenerator<class_5099> {
		private Structure field_23533;

		public class_5048(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void setStructureStarts(BiomeAccess biomeAccess, Chunk chunk, ChunkGenerator<?> chunkGenerator, StructureManager structureManager) {
			if (this.field_23533 == null) {
				this.field_23533 = structureManager.getStructureOrBlank(new Identifier("library"));
			}

			super.setStructureStarts(biomeAccess, chunk, chunkGenerator, structureManager);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public int getSpawnHeight() {
			return 30;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();

			for (int k = 0; k < 16; k++) {
				BlockPos blockPos = new BlockPos(16 * i, k * 16, 16 * j);
				this.field_23533.place(region, blockPos, blockPos, new StructurePlacementData(), 4);
			}
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23468;
		}
	}
}
