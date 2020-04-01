package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5049 extends RandomDimension {
	public class_5049(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5049.class_5050(this.field_23566, method_26573(this.field_23566.getSeed()), class_5099.field_23565);
	}

	@Override
	public boolean hasVisibleSky() {
		return true;
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return OverworldDimension.method_26524(timeOfDay, 24000.0);
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

	public static class class_5050 extends ChunkGenerator<class_5099> {
		private Structure field_23534;

		public class_5050(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void setStructureStarts(BiomeAccess biomeAccess, Chunk chunk, ChunkGenerator<?> chunkGenerator, StructureManager structureManager) {
			if (this.field_23534 == null) {
				this.field_23534 = structureManager.getStructureOrBlank(new Identifier("9x9"));
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
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 64; k++) {
						chunk.setBlockState(mutable.set(i, k, j), Blocks.COBBLESTONE.getDefaultState(), false);
					}
				}
			}
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
			int i = region.getCenterChunkX();
			int j = region.getCenterChunkZ();

			for (int k = 0; k < 16; k++) {
				int l = 16 * i + k;
				if (Math.floorMod(l, 10) == 0) {
					for (int m = 0; m < 16; m++) {
						int n = 16 * j + m;
						if (Math.floorMod(n, 10) == 0) {
							BlockPos blockPos = new BlockPos(l, 64, n);
							this.field_23534.place(region, blockPos, blockPos, new StructurePlacementData(), 4);
						}
					}
				}
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
			return ChunkGeneratorType.field_23469;
		}
	}
}
