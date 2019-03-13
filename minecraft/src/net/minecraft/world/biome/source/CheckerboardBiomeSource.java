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
	private final Biome[] biomes;
	private final int gridSize;

	public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig) {
		this.biomes = checkerboardBiomeSourceConfig.getBiomes();
		this.gridSize = checkerboardBiomeSourceConfig.getSize() + 4;
	}

	@Override
	public Biome getBiome(int i, int j) {
		return this.biomes[Math.abs(((i >> this.gridSize) + (j >> this.gridSize)) % this.biomes.length)];
	}

	@Override
	public Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				int o = Math.abs(((i + m >> this.gridSize) + (j + n >> this.gridSize)) % this.biomes.length);
				Biome biome = this.biomes[o];
				biomes[m * k + n] = biome;
			}
		}

		return biomes;
	}

	@Nullable
	@Override
	public BlockPos method_8762(int i, int j, int k, List<Biome> list, Random random) {
		return null;
	}

	@Override
	public boolean method_8754(StructureFeature<?> structureFeature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(structureFeature, structureFeaturex -> {
			for (Biome biome : this.biomes) {
				if (biome.method_8684(structureFeaturex)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			for (Biome biome : this.biomes) {
				this.topMaterials.add(biome.method_8722().getTopMaterial());
			}
		}

		return this.topMaterials;
	}

	@Override
	public Set<Biome> getBiomesInArea(int i, int j, int k) {
		return Sets.<Biome>newHashSet(this.biomes);
	}
}
