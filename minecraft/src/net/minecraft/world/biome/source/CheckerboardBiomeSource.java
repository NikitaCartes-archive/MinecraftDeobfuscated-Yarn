package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeSource extends BiomeSource {
	private final Biome[] biomeArray;
	private final int gridSize;

	public CheckerboardBiomeSource(Biome[] biomes, int i) {
		super(ImmutableSet.copyOf(biomes));
		this.biomeArray = biomes;
		this.gridSize = i + 2;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource method_27985(long l) {
		return this;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biomeArray[Math.floorMod((biomeX >> this.gridSize) + (biomeZ >> this.gridSize), this.biomeArray.length)];
	}
}
