package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockRotation;
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
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5053 extends RandomDimension {
	public class_5053(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5053.class_5054(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
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

	public static class class_5054 extends ChunkGenerator<class_5099> {
		private Structure field_23536;
		private Structure field_23537;
		private Structure field_23538;

		public class_5054(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void setStructureStarts(BiomeAccess biomeAccess, Chunk chunk, ChunkGenerator<?> chunkGenerator, StructureManager structureManager) {
			if (this.field_23536 == null) {
				this.field_23536 = structureManager.getStructureOrBlank(new Identifier("b_center"));
			}

			if (this.field_23537 == null) {
				this.field_23537 = structureManager.getStructureOrBlank(new Identifier("b_side"));
			}

			if (this.field_23538 == null) {
				this.field_23538 = structureManager.getStructureOrBlank(new Identifier("b_legs"));
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
			BlockPos.Mutable mutable = new BlockPos.Mutable(16 * i + 5, 0, 16 * j + 5);
			StructurePlacementData structurePlacementData = new StructurePlacementData();
			int k = 32;

			for (int l = 0; l < 32; l++) {
				this.field_23538.place(region, mutable, mutable, structurePlacementData, 20);
				mutable.move(0, 4, 0);
			}

			this.field_23536.place(region, mutable, mutable, structurePlacementData, 20);
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setTerrainSeed(2 * i, 2 * j - 1);
			if (chunkRandom.nextBoolean()) {
				mutable.set(16 * i + 10, 128, 16 * j + 4);
				this.field_23537.place(region, mutable, mutable, new StructurePlacementData().setRotation(BlockRotation.CLOCKWISE_180), 20);
			}

			chunkRandom.setTerrainSeed(2 * i, 2 * j + 1);
			if (chunkRandom.nextBoolean()) {
				mutable.set(16 * i + 5, 128, 16 * j + 11);
				this.field_23537.place(region, mutable, mutable, new StructurePlacementData(), 20);
			}

			chunkRandom.setTerrainSeed(2 * i - 1, 2 * j);
			if (chunkRandom.nextBoolean()) {
				mutable.set(16 * i + 4, 128, 16 * j + 5);
				this.field_23537.place(region, mutable, mutable, new StructurePlacementData().setRotation(BlockRotation.CLOCKWISE_90), 20);
			}

			chunkRandom.setTerrainSeed(2 * i + 1, 2 * j);
			if (chunkRandom.nextBoolean()) {
				mutable.set(16 * i + 11, 128, 16 * j + 10);
				this.field_23537.place(region, mutable, mutable, new StructurePlacementData().setRotation(BlockRotation.COUNTERCLOCKWISE_90), 20);
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
			return ChunkGeneratorType.field_23471;
		}
	}
}
