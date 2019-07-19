package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;

public class CheckerboardBiomeSource extends BiomeSource {
	private final Biome[] biomeArray;
	private final int gridSize;

	public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig config) {
		this.biomeArray = config.getBiomes();
		this.gridSize = config.getSize() + 4;
	}

	@Override
	public Biome getBiome(int x, int z) {
		return this.biomeArray[Math.abs(((x >> this.gridSize) + (z >> this.gridSize)) % this.biomeArray.length)];
	}

	@Override
	public Biome[] sampleBiomes(int x, int z, int width, int height, boolean bl) {
		Biome[] biomes = new Biome[width * height];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int k = Math.abs(((x + i >> this.gridSize) + (z + j >> this.gridSize)) % this.biomeArray.length);
				Biome biome = this.biomeArray[k];
				biomes[i * width + j] = biome;
			}
		}

		return biomes;
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int x, int z, int radius, List<Biome> biomes, Random random) {
		return null;
	}

	@Override
	public boolean hasStructureFeature(StructureFeature<?> feature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(feature, structureFeature -> {
			for (Biome biome : this.biomeArray) {
				if (biome.hasStructureFeature(structureFeature)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			for (Biome biome : this.biomeArray) {
				this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
			}
		}

		return this.topMaterials;
	}

	@Override
	public Set<Biome> getBiomesInArea(int x, int z, int radius) {
		return Sets.<Biome>newHashSet(this.biomeArray);
	}
}
