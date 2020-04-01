package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
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

public class class_5063 extends RandomDimension {
	public class_5063(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5063.class_5064(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.5F;
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

	public static class class_5064 extends ChunkGenerator<class_5099> {
		private final List<Block> field_23543 = (List<Block>)Registry.BLOCK
			.stream()
			.filter(block -> !block.method_26477() && !block.hasBlockEntity())
			.collect(ImmutableList.toImmutableList());

		public class_5064(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
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
			ChunkRandom chunkRandom = new ChunkRandom();
			ChunkPos chunkPos = chunk.getPos();
			chunkRandom.setTerrainSeed(chunkPos.x, chunkPos.z);
			float f = chunkRandom.nextFloat() / 10.0F;
			float g = chunkRandom.nextFloat() / 10.0F;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					int k = i - 8;
					int l = j - 8;
					float h = MathHelper.sqrt((float)(k * k + l * l));
					if (h <= 9.0F && h >= 6.0F) {
						if ((int)h == 7) {
							chunk.setBlockState(mutable.set(i, 128, j), Blocks.SMOOTH_QUARTZ.getDefaultState(), false);
						} else {
							chunk.setBlockState(mutable.set(i, 128, j), Blocks.CHISELED_QUARTZ_BLOCK.getDefaultState(), false);
						}
					}
				}
			}

			int i = 10 + chunkRandom.nextInt(75);

			for (int jx = -i; jx < i; jx++) {
				Block block = Util.method_26719(chunkRandom, this.field_23543);
				BlockState blockState = Util.method_26719(chunkRandom, block.getStateManager().getStates());
				int m = 8 + (int)(5.0F * MathHelper.sin(f * (float)jx));
				int n = 8 + (int)(5.0F * MathHelper.sin(g * (float)jx));
				chunk.setBlockState(mutable.set(m, 128 + jx, n), blockState, false);
				chunk.setBlockState(mutable.set(m, 128 - jx, n), blockState, false);
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
			return ChunkGeneratorType.field_23476;
		}
	}
}
