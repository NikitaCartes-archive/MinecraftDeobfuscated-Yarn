package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;

public abstract class RandomDimension extends Dimension {
	protected final World field_23566;

	public RandomDimension(World world, DimensionType dimensionType, float f) {
		super(world, dimensionType, f);
		this.field_23566 = world;
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
		return OverworldDimension.method_26526(this.field_23566, chunkPos, checkMobSpawnValidity);
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
		return OverworldDimension.method_26525(this.field_23566, x, z, checkMobSpawnValidity);
	}

	@Override
	public boolean hasVisibleSky() {
		return false;
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	public static FixedBiomeSource method_26572(Biome biome) {
		FixedBiomeSourceConfig fixedBiomeSourceConfig = BiomeSourceType.FIXED.getConfig(0L).setBiome(biome);
		return new FixedBiomeSource(fixedBiomeSourceConfig);
	}

	public static VanillaLayeredBiomeSource method_26573(long l) {
		return BiomeSourceType.VANILLA_LAYERED.applyConfig(BiomeSourceType.VANILLA_LAYERED.getConfig(l));
	}
}
