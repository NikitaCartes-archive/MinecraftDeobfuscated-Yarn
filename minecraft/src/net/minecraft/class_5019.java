package net.minecraft;

import java.util.Random;
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

public class class_5019 extends RandomDimension {
	private static final BlockState field_23512 = Blocks.LIGHT_BLUE_CONCRETE.getDefaultState();
	private static final BlockState field_23513 = Blocks.AIR.getDefaultState();
	private static final BlockState[] field_23514 = new BlockState[]{
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512
	};
	private static final BlockState[] field_23515 = new BlockState[]{
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23512,
		field_23512,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513,
		field_23513
	};

	public class_5019(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5019.class_5020(this.field_23566, method_26572(Biomes.JUNGLE), class_5099.field_23565);
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

	public static class class_5020 extends ChunkGenerator<class_5099> {
		public class_5020(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
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
			BlockState blockState = Blocks.BLUE_CONCRETE.getDefaultState();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						chunk.setBlockState(mutable.set(i, j, k), blockState, false);
					}
				}
			}

			Random random = new Random((long)(chunkPos.x << 16 + chunkPos.z));
			this.method_26531(chunk, 0, 0, random.nextBoolean() ? class_5019.field_23514 : class_5019.field_23515);
			this.method_26531(chunk, 0, 8, random.nextBoolean() ? class_5019.field_23514 : class_5019.field_23515);
			this.method_26531(chunk, 8, 0, random.nextBoolean() ? class_5019.field_23514 : class_5019.field_23515);
			this.method_26531(chunk, 8, 8, random.nextBoolean() ? class_5019.field_23514 : class_5019.field_23515);
		}

		private void method_26531(Chunk chunk, int i, int j, BlockState[] blockStates) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = 0; k < 8; k++) {
				for (int l = 0; l < 8; l++) {
					BlockState blockState = blockStates[k * 8 + l];

					for (int m = 16; m < 32; m++) {
						chunk.setBlockState(mutable.set(k + i, m, l + j), blockState, false);
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
			return ChunkGeneratorType.field_23459;
		}
	}
}
