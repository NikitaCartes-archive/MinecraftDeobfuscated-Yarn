package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.DirectionTransformation;
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
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class class_5083 extends RandomDimension {
	public class_5083(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5083.class_5084(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
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

	public static class class_5084 extends ChunkGenerator<class_5099> {
		private static final BlockState field_23556 = Blocks.FIRE
			.getDefaultState()
			.with(FireBlock.NORTH, Boolean.valueOf(true))
			.with(FireBlock.SOUTH, Boolean.valueOf(true))
			.with(FireBlock.EAST, Boolean.valueOf(true))
			.with(FireBlock.WEST, Boolean.valueOf(true))
			.with(FireBlock.UP, Boolean.valueOf(true));
		final SimpleBlockStateProvider field_23555 = new SimpleBlockStateProvider(field_23556);

		public class_5084(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
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
			ChunkPos chunkPos = chunk.getPos();
			if (chunkPos.z == 0) {
				this.method_26564(chunk, chunkPos.x);
			}
		}

		private void method_26564(Chunk chunk, int i) {
			if (i >= 0 && i < " We apologise for the inconvenience.".length()) {
				char c = " We apologise for the inconvenience.".charAt(i);
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				class_5102.method_26586(BlockPos.ORIGIN, new class_5104(this.field_23555, c, DirectionTransformation.IDENTITY), blockPos -> {
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 0, 2 * blockPos.getY() + 100, 2 * blockPos.getZ() + 0), field_23556, false);
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 1, 2 * blockPos.getY() + 100, 2 * blockPos.getZ() + 1), field_23556, false);
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 1, 2 * blockPos.getY() + 100, 2 * blockPos.getZ() + 0), field_23556, false);
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 0, 2 * blockPos.getY() + 100, 2 * blockPos.getZ() + 1), field_23556, false);
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 0, 2 * blockPos.getY() + 101, 2 * blockPos.getZ() + 0), field_23556, false);
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 1, 2 * blockPos.getY() + 101, 2 * blockPos.getZ() + 1), field_23556, false);
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 1, 2 * blockPos.getY() + 101, 2 * blockPos.getZ() + 0), field_23556, false);
					chunk.setBlockState(mutable.set(2 * blockPos.getX() + 0, 2 * blockPos.getY() + 101, 2 * blockPos.getZ() + 1), field_23556, false);
				});
			}
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
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
			return ChunkGeneratorType.field_23451;
		}
	}
}
