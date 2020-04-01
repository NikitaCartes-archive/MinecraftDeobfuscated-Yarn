package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5042 extends RandomDimension {
	public class_5042(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5042.class_5043(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 12000.0F;
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

	public static class class_5043 extends ChunkGenerator<class_5099> {
		private Structure field_23529;

		public class_5043(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void setStructureStarts(BiomeAccess biomeAccess, Chunk chunk, ChunkGenerator<?> chunkGenerator, StructureManager structureManager) {
			if (this.field_23529 == null) {
				this.field_23529 = structureManager.getStructureOrBlank(new Identifier("llama"));
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
			BlockState blockState = Blocks.BEDROCK.getDefaultState();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 256; j++) {
					for (int k = 0; k < 16; k++) {
						chunk.setBlockState(mutable.set(i, j, k), blockState, false);
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
			BlockPos blockPos = new BlockPos(16 * i + 8, 64, 16 * j + 8);
			this.field_23529.place(region, blockPos, blockPos, new StructurePlacementData(), 4);
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
			return ChunkGeneratorType.field_23466;
		}
	}
}
