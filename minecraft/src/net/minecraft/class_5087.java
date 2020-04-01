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
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class class_5087 extends RandomDimension {
	public class_5087(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.0F);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5087.class_5088(this.field_23566, method_26572(Biomes.PLAINS), class_5099.field_23565);
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

	public static class class_5088 extends ChunkGenerator<class_5099> {
		public class_5088(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
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
			ChunkPos chunkPos = chunk.getPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			if (chunkPos.x == 0 && chunkPos.z == 0) {
				BlockState blockState = Blocks.LIME_STAINED_GLASS.getDefaultState();
				BlockState blockState2 = Blocks.RED_STAINED_GLASS.getDefaultState();
				BlockState blockState3 = Blocks.BLUE_STAINED_GLASS.getDefaultState();
				chunk.setBlockState(mutable.set(0, 0, 0), Blocks.DIAMOND_BLOCK.getDefaultState(), false);

				for (int i = 1; i <= 4; i++) {
					chunk.setBlockState(mutable.set(0, i, 0), blockState, false);
					chunk.setBlockState(mutable.set(i, 0, 0), blockState2, false);
					chunk.setBlockState(mutable.set(0, 0, i), blockState3, false);
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
			return ChunkGeneratorType.field_23453;
		}
	}
}
