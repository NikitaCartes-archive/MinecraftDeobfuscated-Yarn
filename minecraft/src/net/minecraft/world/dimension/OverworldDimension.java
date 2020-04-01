package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class OverworldDimension extends Dimension {
	public OverworldDimension(World world, DimensionType type) {
		super(world, type, 0.0F);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		return (ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.getLevelProperties().getGeneratorOptions().createChunkGenerator(this.world);
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
		return method_26526(this.world, chunkPos, checkMobSpawnValidity);
	}

	@Nullable
	public static BlockPos method_26526(World world, ChunkPos chunkPos, boolean bl) {
		for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); i++) {
			for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); j++) {
				BlockPos blockPos = method_26525(world, i, j, bl);
				if (blockPos != null) {
					return blockPos;
				}
			}
		}

		return null;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
		return method_26525(this.world, x, z, checkMobSpawnValidity);
	}

	@Nullable
	public static BlockPos method_26525(World world, int i, int j, boolean bl) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(i, 0, j);
		Biome biome = world.getBiome(mutable);
		BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
		if (bl && !blockState.getBlock().isIn(BlockTags.VALID_SPAWN)) {
			return null;
		} else {
			WorldChunk worldChunk = world.getChunk(i >> 4, j >> 4);
			int k = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, i & 15, j & 15);
			if (k < 0) {
				return null;
			} else if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i & 15, j & 15) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, i & 15, j & 15)
				)
			 {
				return null;
			} else {
				for (int l = k + 1; l >= 0; l--) {
					mutable.set(i, l, j);
					BlockState blockState2 = world.getBlockState(mutable);
					if (!blockState2.getFluidState().isEmpty()) {
						break;
					}

					if (blockState2.equals(blockState)) {
						return mutable.up().toImmutable();
					}
				}

				return null;
			}
		}
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return method_26524(timeOfDay, 24000.0);
	}

	public static float method_26524(long l, double d) {
		double e = MathHelper.fractionalPart((double)l / d - 0.25);
		double f = 0.5 - Math.cos(e * Math.PI) / 2.0;
		return (float)(e * 2.0 + f) / 3.0F;
	}

	@Override
	public boolean hasVisibleSky() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d.multiply((double)(tickDelta * 0.94F + 0.06F), (double)(tickDelta * 0.94F + 0.06F), (double)(tickDelta * 0.91F + 0.09F));
	}

	@Override
	public boolean canPlayersSleep() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}
}
